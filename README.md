# IranMapSvg
Iran map android library

![screenshot](https://github.com/MohammadRezaei92/IranMapSvg/blob/master/screenshot/demo.gif)
## How to install
### Gradle
add this line to your module build.gradle dependecies block:
```
implementation 'com.github.MohammadRezaei92:IranMapSvg:1.0.0'
```

## How to use
### XML
```xml
<rezaei.mohammad.iranmap.IranMapView
        android:id="@+id/iranMap"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:imProvinceBackgroundColor="#81D4FA"
        app:imProvinceActiveBackgroundColor="#03A9F4"
        app:imProvinceStrokeColor="#039BE5"
        app:imProvinceSelectByClick="true"
        app:imAnimationDuration="200"
        app:imProvinceMultiSelect="true"
        app:imMapAppearWithAnimation="true" />
```
### Kotlin
```kotlin
        // activate a province
        iranMap.activeProvince(Province.Esfahan,Color.CYAN,Color.YELLOW,true)
        //deactivate a province
        iranMap.deActiveProvince(Province.Tehran)
        //add title to a province
        iranMap.addTitle(Province.Alborz,"population:63832", Typeface.SANS_SERIF,Color.WHITE)
        //remove title
        iranMap.removeTitle(Province.Alborz)
        //get list of active provinces
        iranMap.selectedProvinces
```
### attrs

|Name|Default value|Description|
|:---:|:---:|:---:|
|imProvinceBackgroundColor|Color.BLACK|Default province background color|
|imProvinceActiveBackgroundColor|Color.CYAN|Actice color of province|
|imProvinceStrokeColor|Color.WHITE|Stroke color of province|
|imProvinceSelectByClick|true|Make provinces clickable|
|imProvinceMultiSelect|false|Select multi provinces|
|imMapAppearWithAnimation|false|Appear map with animation|
|imAnimationDuration|200|Map and provinces animation duration|
