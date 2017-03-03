#!/bin/bash

mkdir select
mkdir unselect
mkdir used
mkdir color_select
mkdir color_unselect
mkdir color_used
mkdir category

if [ $1 = "select" ]; then

	echo "Start select file rename !!"

	cd select
	mv ~/Downloads/select*.zip .
	unzip select*.zip

	mv drawable-hdpi/select.png   		/Users/actmember/workspace/angelman/app/src/main/res/drawable-hdpi/icon_$2_select.png
	mv drawable-mdpi/select.png   		/Users/actmember/workspace/angelman/app/src/main/res/drawable-mdpi/icon_$2_select.png
	mv drawable-xhdpi/select.png  		/Users/actmember/workspace/angelman/app/src/main/res/drawable-xhdpi/icon_$2_select.png
	mv drawable-xxhdpi/select.png 		/Users/actmember/workspace/angelman/app/src/main/res/drawable-xxhdpi/icon_$2_select.png
	mv drawable-xxxhdpi/select.png		/Users/actmember/workspace/angelman/app/src/main/res/drawable-xxxhdpi/icon_$2_select.png

elif [ $1 = "unselect" ]; then

	echo "Start unselect file rename !!"

	cd unselect
	mv ~/Downloads/unselect*.zip .
	unzip unselect*.zip

	mv drawable-hdpi/unselect.png   	/Users/actmember/workspace/angelman/app/src/main/res/drawable-hdpi/icon_$2_unselect.png
	mv drawable-mdpi/unselect.png   	/Users/actmember/workspace/angelman/app/src/main/res/drawable-mdpi/icon_$2_unselect.png
	mv drawable-xhdpi/unselect.png  	/Users/actmember/workspace/angelman/app/src/main/res/drawable-xhdpi/icon_$2_unselect.png
	mv drawable-xxhdpi/unselect.png 	/Users/actmember/workspace/angelman/app/src/main/res/drawable-xxhdpi/icon_$2_unselect.png
	mv drawable-xxxhdpi/unselect.png	/Users/actmember/workspace/angelman/app/src/main/res/drawable-xxxhdpi/icon_$2_unselect.png

elif [ $1 = "icon_used" ]; then

	echo "Start icon used file rename !!"

	cd used 
	mv ~/Downloads/used*.zip .
	unzip used*.zip

	mv drawable-hdpi/used.png   	/Users/actmember/workspace/angelman/app/src/main/res/drawable-hdpi/icon_$2_used.png
	mv drawable-mdpi/used.png   	/Users/actmember/workspace/angelman/app/src/main/res/drawable-mdpi/icon_$2_used.png
	mv drawable-xhdpi/used.png  	/Users/actmember/workspace/angelman/app/src/main/res/drawable-xhdpi/icon_$2_used.png
	mv drawable-xxhdpi/used.png 	/Users/actmember/workspace/angelman/app/src/main/res/drawable-xxhdpi/icon_$2_used.png
	mv drawable-xxxhdpi/used.png	/Users/actmember/workspace/angelman/app/src/main/res/drawable-xxxhdpi/icon_$2_used.png


elif [ $1 = "color_unselect" ]; then

	echo "Start color unselect file rename !!"

	cd color_unselect
	mv ~/Downloads/normal*.zip .
	unzip normal*.zip

	mv drawable-hdpi/normal_*.png   	/Users/actmember/workspace/angelman/app/src/main/res/drawable-hdpi/icon_color_$2_unselect.png
	mv drawable-mdpi/normal_*.png   	/Users/actmember/workspace/angelman/app/src/main/res/drawable-mdpi/icon_color_$2_unselect.png
	mv drawable-xhdpi/normal_*.png  	/Users/actmember/workspace/angelman/app/src/main/res/drawable-xhdpi/icon_color_$2_unselect.png
	mv drawable-xxhdpi/normal_*.png 	/Users/actmember/workspace/angelman/app/src/main/res/drawable-xxhdpi/icon_color_$2_unselect.png
	mv drawable-xxxhdpi/normal_*.png	/Users/actmember/workspace/angelman/app/src/main/res/drawable-xxxhdpi/icon_color_$2_unselect.png

elif [ $1 = "color_select" ]; then

	echo "Start color select rename !!"

	cd color_select
	mv ~/Downloads/now_select*.zip .
	unzip now_select*.zip

	mv drawable-hdpi/now_select*.png   	/Users/actmember/workspace/angelman/app/src/main/res/drawable-hdpi/icon_color_$2_select.png
	mv drawable-mdpi/now_select*.png   	/Users/actmember/workspace/angelman/app/src/main/res/drawable-mdpi/icon_color_$2_select.png
	mv drawable-xhdpi/now_select*.png  	/Users/actmember/workspace/angelman/app/src/main/res/drawable-xhdpi/icon_color_$2_select.png
	mv drawable-xxhdpi/now_select*.png 	/Users/actmember/workspace/angelman/app/src/main/res/drawable-xxhdpi/icon_color_$2_select.png
	mv drawable-xxxhdpi/now_select*.png	/Users/actmember/workspace/angelman/app/src/main/res/drawable-xxxhdpi/icon_color_$2_select.png

elif [ $1 = "color_used" ]; then

	echo "Start color used rename !!"

	cd color_used 
	mv ~/Downloads/selected_*.zip .
	unzip selected_*.zip

	mv drawable-hdpi/selected_*.png   	/Users/actmember/workspace/angelman/app/src/main/res/drawable-hdpi/icon_color_$2_used.png
	mv drawable-mdpi/selected_*.png   	/Users/actmember/workspace/angelman/app/src/main/res/drawable-mdpi/icon_color_$2_used.png
	mv drawable-xhdpi/selected_*.png  	/Users/actmember/workspace/angelman/app/src/main/res/drawable-xhdpi/icon_color_$2_used.png
	mv drawable-xxhdpi/selected_*.png 	/Users/actmember/workspace/angelman/app/src/main/res/drawable-xxhdpi/icon_color_$2_used.png
	mv drawable-xxxhdpi/selected_*.png	/Users/actmember/workspace/angelman/app/src/main/res/drawable-xxxhdpi/icon_color_$2_used.png

elif [ $1 = "category" ]; then

	echo "Start category file rename !!"

	cd category
	mv ~/Downloads/*cartegory*.zip .
	unzip *cartegory*.zip

	mv drawable-hdpi/*cartegory.png   	/Users/actmember/workspace/angelman/app/src/main/res/drawable-hdpi/icon_$2_menu.png
	mv drawable-mdpi/*cartegory.png   	/Users/actmember/workspace/angelman/app/src/main/res/drawable-mdpi/icon_$2_menu.png
	mv drawable-xhdpi/*cartegory.png  	/Users/actmember/workspace/angelman/app/src/main/res/drawable-xhdpi/icon_$2_menu.png
	mv drawable-xxhdpi/*cartegory.png 	/Users/actmember/workspace/angelman/app/src/main/res/drawable-xxhdpi/icon_$2_menu.png
	mv drawable-xxxhdpi/*cartegory.png	/Users/actmember/workspace/angelman/app/src/main/res/drawable-xxxhdpi/icon_$2_menu.png

fi

cd ..

rm -rf select
rm -rf unselect
rm -rf used
rm -rf color_select
rm -rf color_unselect
rm -rf color_used
rm -rf category



