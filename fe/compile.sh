#!/bin/bash
harp compile
sed  '1s/http:\/\/127.0.0.1:8082//' www/public/constant.js > tmp.txt
cat tmp.txt > www/public/constant.js
cp -R  www/* ../src/main/resources/static

