import java.lang.Exception
import java.lang.StringBuilder
import java.net.Socket

fun main(args: Array<String>) {
    println("hello world from kolin land")
    val httpc = Httpckt()
    httpc.post()
}

class Httpckt {

    fun main(args: Array<String>) {

    }





    fun post(){
        try{
            val socket = Socket("httpbin.org", 80)


            val inputStream = socket.getInputStream()
            val outputStream = socket.getOutputStream()

            val body = "key1=value1&key2=value2"

            val request = "POST /post HTTP/1.0\r\n" +
                    "Content-Type:application/x-www-form-urlencoded\r\n" +
                    "Content-Length: ${body.length}\r\n" +
                    "\r\n" +
                    body

            outputStream.write(request.toByteArray())
            outputStream.flush()

            val response = StringBuilder()

            var data = inputStream.read()

            while(data != -1) {
                response.append(data.toChar())
                data = inputStream.read()
            }

            print(response)
            socket.close()

        } catch (e: Exception) {

        }
    }

}