# glassmorphism-box

![image](https://github.com/taufik-kurahman/glassmorphism-box/blob/master/example.jpg)

repositories:
```
maven { url = uri("https://www.jitpack.io") }
```
dependencies:
```
implementation("io.github.taufik-kurahman:glassmorphism-box:1.0.2")
```

Usage: 

```
val capturer = rememberCapturer()
Box(
    modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
) {
    Image(
        painterResource(R.drawable.your_image_name),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .matchParentSize()
            .glassmorphismBackground(capturer)
    )
    GlassmorphismBox(
        modifier = Modifier.size(150.dp),
        capturer = capturer,
        contentAlignment = Alignment.Center
    ) {
        // Your content
    }
}
```
See [example](https://github.com/taufik-kurahman/glassmorphism-box/blob/master/app/src/main/java/io/github/taufik_kurahman/glassmorphism_box_example/MainActivity.kt)
