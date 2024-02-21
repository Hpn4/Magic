#!/bin/bash

for i in `ls`; do
	if [ i != "resize.sh" ]; then
		path=$i"/planeswalker"
		echo "$path/card.jpg"
		convert "$path/card.jpg" -resize "375x523" "$path/card.jpg"
		convert "$path/card1.jpg" -resize "375x523" "$path/card1.jpg"
		convert "$path/stamp.jpg" -resize "46x26" "$path/stamp.jpg"
	fi
done

convert "stat.png" -resize "68x38"! "stat.png"