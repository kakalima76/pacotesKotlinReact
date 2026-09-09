const fs = require('fs');
const path = require('path');

const mapboxDir = path.join(process.cwd(), 'node_modules', '@rnmapbox', 'maps');

const gradleFile = path.join(mapboxDir, 'android', 'build.gradle');

const bitmapFile = path.join(
  mapboxDir,
  'android',
  'src',
  'main',
  'java',
  'com',
  'rnmapbox',
  'rnmbx',
  'utils',
  'DownloadMapImageTask.kt',
);

if (!fs.existsSync(mapboxDir)) {
  console.log('Mapbox não instalado. Nenhuma alteração necessária.');
  process.exit(0);
}

// --------------------------------------------------
// Correção 1: ProGuard para AGP 9
// --------------------------------------------------

if (fs.existsSync(gradleFile)) {
  let content = fs.readFileSync(gradleFile, 'utf8');

  const oldValue = "getDefaultProguardFile('proguard-android.txt')";

  const newValue = "getDefaultProguardFile('proguard-android-optimize.txt')";

  if (content.includes(oldValue)) {
    content = content.replace(oldValue, newValue);
    fs.writeFileSync(gradleFile, content);

    console.log('Mapbox: ProGuard corrigido para AGP 9.');
  } else {
    console.log('Mapbox: ProGuard já está corrigido.');
  }
}

// --------------------------------------------------
// Correção 2: Kotlin 2.2 / Bitmap nullable
// --------------------------------------------------

if (fs.existsSync(bitmapFile)) {
  let content = fs.readFileSync(bitmapFile, 'utf8');

  const oldValue = 'image.underlyingBitmap.copy(Bitmap.Config.ARGB_8888, true)';

  const newValue =
    'image.underlyingBitmap!!.copy(Bitmap.Config.ARGB_8888, true)';

  if (content.includes(oldValue)) {
    content = content.replace(oldValue, newValue);
    fs.writeFileSync(bitmapFile, content);

    console.log('Mapbox: correção Kotlin/Bitmap aplicada.');
  } else if (content.includes(newValue)) {
    console.log('Mapbox: correção Kotlin/Bitmap já aplicada.');
  } else {
    console.log('Mapbox: padrão Kotlin/Bitmap não encontrado.');
  }
}

// --------------------------------------------------
// Correção 3: React Native Gesture Handler
// --------------------------------------------------

const gestureHandlerGradle = path.join(
  process.cwd(),
  'node_modules',
  'react-native-gesture-handler',
  'android',
  'build.gradle',
);

if (fs.existsSync(gestureHandlerGradle)) {
  let content = fs.readFileSync(gestureHandlerGradle, 'utf8');

  const oldCompileSdk = 'compileSdkVersion safeExtGet("compileSdkVersion", 33)';

  const newCompileSdk = 'compileSdk safeExtGet("compileSdkVersion", 33)';

  if (content.includes(oldCompileSdk)) {
    content = content.replace(oldCompileSdk, newCompileSdk);

    console.log('Gesture Handler: compileSdk corrigido para AGP 9.');
  }

  const oldNode =
    'commandLine("node", "--print", "require.resolve(\'react-native/package.json\')")';

  const newNode =
    'commandLine("/opt/homebrew/bin/node", "--print", "require.resolve(\'react-native/package.json\')")';

  if (content.includes(oldNode)) {
    content = content.replace(oldNode, newNode);

    console.log('Gesture Handler: caminho do Node corrigido.');
  }

  fs.writeFileSync(gestureHandlerGradle, content);
}

// --------------------------------------------------
// Correção 4: React Native Reanimated
// --------------------------------------------------

const reanimatedGradle = path.join(
  process.cwd(),
  'node_modules',
  'react-native-reanimated',
  'android',
  'build.gradle.kts',
);

if (fs.existsSync(reanimatedGradle)) {
  let content = fs.readFileSync(reanimatedGradle, 'utf8');

  const oldNode =
    'commandLine("node", "--print", "require.resolve(\'react-native/package.json\')")';

  const newNode =
    'commandLine("/opt/homebrew/bin/node", "--print", "require.resolve(\'react-native/package.json\')")';

  const nodeCommands = [
    'commandLine("node", "--print", "require.resolve(\'react-native/package.json\')")',
    'commandLine("node", "./../scripts/validate-react-native-version.js", REACT_NATIVE_VERSION)',
    'commandLine("node", "./../scripts/validate-worklets-build.js")',
  ];

  const nodeCommandsFixed = [
    'commandLine("/opt/homebrew/bin/node", "--print", "require.resolve(\'react-native/package.json\')")',
    'commandLine("/opt/homebrew/bin/node", "./../scripts/validate-react-native-version.js", REACT_NATIVE_VERSION)',
    'commandLine("/opt/homebrew/bin/node", "./../scripts/validate-worklets-build.js")',
  ];

  let changed = false;

  for (let i = 0; i < nodeCommands.length; i++) {
    if (content.includes(nodeCommands[i])) {
      content = content.replaceAll(nodeCommands[i], nodeCommandsFixed[i]);

      changed = true;
    }
  }

  if (changed) {
    console.log('Reanimated: caminhos do Node corrigidos.');
  } else {
    console.log('Reanimated: caminhos do Node já estão corrigidos.');
  }
  fs.writeFileSync(reanimatedGradle, content);
}
