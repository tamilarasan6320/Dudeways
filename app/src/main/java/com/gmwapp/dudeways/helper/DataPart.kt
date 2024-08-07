package com.gmwapp.dudeways.helper

class DataPart {
    @JvmField
    var fileName: String
    @JvmField
    var content: ByteArray
    @JvmField
    var type: String? = null


    constructor(name: String, data: ByteArray) {
        fileName = name
        content = data
    }

    constructor(name: String, data: ByteArray, mimeType: String?) {
        fileName = name
        content = data
        type = mimeType
    }
}
