package com.example.util
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
object BarcodeDecoder {
 val formats=listOf(BarcodeFormat.QR_CODE,BarcodeFormat.DATA_MATRIX,BarcodeFormat.EAN_13,BarcodeFormat.EAN_8,BarcodeFormat.UPC_A,BarcodeFormat.UPC_E,BarcodeFormat.CODE_128,BarcodeFormat.CODE_39,BarcodeFormat.ITF)
 fun decode(data:ByteArray,width:Int,height:Int):String?=decodeResult(data,width,height)?.text
 fun decodeResult(data:ByteArray,width:Int,height:Int):com.google.zxing.Result?{
  if(width<=0||height<=0||data.size<width*height)return null
  val reader=MultiFormatReader().apply{setHints(mapOf(DecodeHintType.POSSIBLE_FORMATS to formats,DecodeHintType.TRY_HARDER to true))}
  var bytes=data;var w=width;var h=height
  repeat(4){
   val source=PlanarYUVLuminanceSource(bytes,w,h,0,0,w,h,false)
   for(inverted in listOf(false,true))try{return reader.decodeWithState(BinaryBitmap(HybridBinarizer(if(inverted)source.invert() else source)))}catch(_:NotFoundException){}finally{reader.reset()}
   val rotated=ByteArray(w*h);for(y in 0 until h)for(x in 0 until w)rotated[x*h+(h-y-1)]=bytes[y*w+x];bytes=rotated;val old=w;w=h;h=old
  }
  return null
 }
}
