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

  const oldValue =
    'image.underlyingBitmap.copy(Bitmap.Config.ARGB_8888, true)!!';

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
