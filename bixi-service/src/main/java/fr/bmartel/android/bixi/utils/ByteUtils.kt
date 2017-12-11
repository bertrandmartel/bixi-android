package fr.bmartel.android.bixi.utils

object ByteUtils {

    fun concatByteArray(vararg data: ByteArray): ByteArray {
        var length = 0
        for (item: ByteArray in data) {
            length += item.size
        }
        val resp = ByteArray(length)
        var offset = 0
        for (item: ByteArray in data) {
            if (item != null) {
                System.arraycopy(item, 0, resp, offset, item.size)
                offset += item.size
            }
        }
        return resp
    }

    fun byteArrayToInt(b: ByteArray): Int {
        return (b[3].toInt() and 0xFF) or (
                (b[2].toInt() and 0xFF) shl 8) or (
                (b[1].toInt() and 0xFF) shl 16) or (
                (b[0].toInt() and 0xFF) shl 24)
    }
}
