
package com.taletable.android.lib.functor;


//
//        else if (this.getValue() instanceof SerialBitmap)
//        {
//            Bitmap bitmap = ((SerialBitmap) this.getValue()).getBitmap();
//            if (bitmap != null) {
//                byte[] bytes = Util.getBytes(bitmap);
//                return SQLValue.newBlob(bytes);
//            } else {
//                return SQLValue.newNull();
//            }
//        }
//        else if (this.valueClass.isAssignableFrom(SerialBitmap.class))
//        {
//            byte[] bitmapBlob = sqlValue.getBlob();
//            if (bitmapBlob != null) {
//                Bitmap bitmap = Util.getImage(bitmapBlob);
//                SerialBitmap serialBitmap = new SerialBitmap(bitmap);
//                this.setValue((A) serialBitmap);
//            } else {
//                this.setValue(null);
//            }
//        }

