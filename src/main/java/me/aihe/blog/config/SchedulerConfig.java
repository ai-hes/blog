package me.aihe.blog.config;

import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import me.aihe.blog.constant.OssConstants;
import me.aihe.blog.util.OssUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * @author : aihe
 * @date : 2020-01-18
 * @Description: 定期检查是否有不正常的图片
 */
@Configuration
public class SchedulerConfig {
//    https://upload-images.jianshu.io/upload_images/426671-582400fc504d134e.png
//    426671-f1b7ac8e63d1e8d0.png

    @Scheduled(cron = "0 0 0 * * *")
    public void reUpload() {
        ObjectListing objectListing = null;
        do {
            // MaxKey默认值为100，最大值为1000。
            ListObjectsRequest request = new ListObjectsRequest(OssConstants.BUCKET_NAME)
                    .withPrefix(OssConstants.PREFIX)
                    .withMaxKeys(1000);
            if (objectListing != null) {
                request.setMarker(objectListing.getNextMarker());
            }
            objectListing = OssUtils.getOssClient().listObjects(request);
            List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
            for (OSSObjectSummary objectSummary : sums) {
                if (objectSummary.getSize() > 0 && objectSummary.getSize() < 1000) {
                    // 代表上传的有问题的照片需要重新上传
                    String key = objectSummary.getKey().replace(OssConstants.PREFIX, "");
                    String originalUrl = String.format(OssConstants.JIANSHU_URL_FORMMAT, key);
                    OssUtils.uploadUrl(originalUrl,true);
                }
            }
        } while (objectListing.isTruncated());
    }

}
