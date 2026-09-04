package com.zzkj.structure.util;

class StringUtils {
    companion object {

        @JvmStatic
        fun isEmpty(str: CharSequence?): Boolean {
            return str.isNullOrEmpty()
        }

        @JvmStatic
        fun isNotEmpty(str: CharSequence?): Boolean {
            return !isEmpty(str)
        }

        @JvmStatic
        fun equals(a: CharSequence?, b: CharSequence?): Boolean {
            return a == b
        }

        @JvmStatic
        fun notEquals(a: CharSequence?, b: CharSequence?): Boolean {
            return !equals(a, b)
        }

        @JvmStatic
        fun length(str: CharSequence?): Int {
            return str?.length ?: 0
        }

//        /**
//         * @param c
//         * @return 是否是英文字母
//         */
//        @JvmStatic
//        fun isLetter(c: Char): Boolean {
//            return c in 'a'..'z' || c in 'A'..'Z'
//        }
//
//        fun getDimString(
//            str: String,
//            startLength: Int,
//            endLength: Int,
//            dimStrCount: Int,
//            dim: String?
//        ): String? {
//            var startLength = startLength
//            var endLength = endLength
//            if (StringUtils.length(str) < 2) {
//                return str
//            }
//            if (str.length < startLength + endLength) {
//                endLength = str.length / 2
//                startLength = endLength
//            }
//            val builder = java.lang.StringBuilder(dimStrCount)
//            for (i in 0 until dimStrCount) {
//                builder.append(dim)
//            }
//            return (str.substring(0, startLength)
//                    + builder.toString()
//                    + str.substring(str.length - endLength))
//        }
//
//        fun getDimPhone(phone: String): String? {
//            return if (StringUtils.isEmpty(phone) || phone.length < 5) {
//                phone
//            } else getDimString(phone, 2, 2, 1, "*******")
//        }
//
//        fun getDimEmail(email: String): String? {
//            if (StringUtils.isEmpty(email) || email.length < 5) {
//                return email
//            }
//            var dimEmail = email[0].toString() + "******"
//            val atIndex = email.lastIndexOf('@')
//            val dotIndex = email.lastIndexOf('.')
//            if (atIndex != -1) {
//                dimEmail += if (dotIndex - atIndex > 1) {
//                    (email.substring(email.indexOf('@'), email.indexOf('@') + 2)
//                            + "***"
//                            + email.substring(email.lastIndexOf('.')))
//                } else if (dotIndex != -1) {
//                    "@***" + email.substring(email.lastIndexOf('.'))
//                } else {
//                    "@***"
//                }
//            }
//            return dimEmail
//        }
//
//        fun join(arr: Array<String?>?, joiner: String?): String? {
//            if (arr == null) return ""
//            if (arr.size == 1) return arr[0]
//            val builder = StringBuilder(arr[0])
//            for (i in 1 until arr.size) {
//                builder.append(joiner).append(arr[i])
//            }
//            return builder.toString()
//        }
//
//        fun join(arr: List<*>?, joiner: String?): String? {
//            if (arr == null || arr.isEmpty()) return ""
//            if (arr.size == 1) return arr[0].toString()
//            val builder = StringBuilder(arr[0].toString())
//            for (i in 1 until arr.size) {
//                builder.append(joiner).append(arr[i].toString())
//            }
//            return builder.toString()
//        }
    }
}
