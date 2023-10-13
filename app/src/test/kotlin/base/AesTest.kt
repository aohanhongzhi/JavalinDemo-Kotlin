package base

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

fun main() {
    val text = "oooooo灰灰"
    val key = "qwertyuiopasdfgr"
    val iv = "qwertyuiopasdfgh".toByteArray()

    println("明文：$text")
    println("key：$key")
    println("iv：${String(iv)}")

    // 加密
    val encrypted = cbcEncrypt(text.toByteArray(), key.toByteArray(), iv)
    val encryptedToString = bytesToHex(encrypted)
    println("加密后的密文：$encryptedToString")

    // 解密
    val decrypted = cbcDecrypt(hexToBytes(encryptedToString), key.toByteArray(), iv)
    val decryptedText = String(decrypted)
    println("解密后的明文：$decryptedText")
}

fun cbcEncrypt(text: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val secretKey = SecretKeySpec(key, "AES")
    val ivSpec = IvParameterSpec(iv)
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
    return cipher.doFinal(text)
}

fun cbcDecrypt(encrypted: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val secretKey = SecretKeySpec(key, "AES")
    val ivSpec = IvParameterSpec(iv)
    cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
    return cipher.doFinal(encrypted)
}

fun bytesToHex(bytes: ByteArray): String {
    val sb = StringBuilder()
    for (b in bytes) {
        sb.append(String.format("%02x", b.toInt() and 0xFF))
    }
    return sb.toString()
}

fun hexToBytes(hex: String): ByteArray {
    val length = hex.length
    val bytes = ByteArray(length / 2)
    for (i in 0 until length step 2) {
        bytes[i / 2] =
            ((Character.digit(hex[i], 16) shl 4) + Character.digit(hex[i + 1], 16)).toByte()
    }
    return bytes
}
