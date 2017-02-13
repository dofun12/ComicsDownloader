#!/bin/bash
find ../ -type d|awk '{if($1~/[0-9]/){x=substr($1,4);system("rar a " x ".rar ../"x)}}'
find ./ -type f -name '*.rar'|awk '{x="The Walking Dead - "substr($1,3,length($1)-6)".cbr";system("mv "$1" \""x"\"")}'

