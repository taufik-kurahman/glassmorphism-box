# glassmorphism-box

![image](https://github.com/taufik-kurahman/glassmorphism-box/blob/master/example.jpg)

repositories:
```
maven { url = uri("https://www.jitpack.io") }
```
dependencies:
```
implementation("io.github.taufik-kurahman:glassmorphism-box:1.0.4")
```

Usage: 

```
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            YourAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Example(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Example(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    val capturer = rememberCapturer()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            Image(
                painterResource(R.drawable.img_1),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .glassmorphismBackground(capturer)
            )
            GlassmorphismBox(
                modifier = Modifier
                    .size(150.dp)
                    .border(1.dp, Color.Red),
                capturer = capturer
            ) {
                // Your content
            }
        }
    }
}
```
See [example](https://github.com/taufik-kurahman/glassmorphism-box/blob/master/app/src/main/java/io/github/taufik_kurahman/glassmorphism_box_example/MainActivity.kt)
