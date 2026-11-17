def isOpen(port: Int): Boolean =
  try {
    import java.net.Socket
    import java.net.InetSocketAddress
    val socket = new Socket()
    socket.connect(new InetSocketAddress("localhost", port))
    socket.close()
    true
  } catch {
    case e: Exception => false
  }

def awaitOpen(port: Int, retries: Int = 40): Unit =
  if (!isOpen(port)) {
    if (retries > 0) {
      Thread.sleep(250)
      awaitOpen(port, retries - 1)
    } else {
      throw new Exception(s"expected port $port to be open")
    }
  } else { Thread.sleep(5000) }

def awaitClosed(port: Int, retries: Int = 40): Unit =
  if (isOpen(port)) {
    if (retries > 0) {
      Thread.sleep(250)
      awaitClosed(port, retries - 1)
    } else {
      throw new Exception(s"expected port $port to be closed")
    }
  }

val awaitPort = inputKey[Unit]("await a listening port")

awaitPort := {
  complete.DefaultParsers.spaceDelimited("<arg>").parsed.toList match {
    case List(port) => awaitOpen(port.toInt)
    case _ => throw new Exception("Usage: await <port>")
  }
}

val awaitPortClosed = inputKey[Unit]("await a closed port")

awaitPortClosed := {
  complete.DefaultParsers.spaceDelimited("<arg>").parsed.toList match {
    case List(port) => awaitClosed(port.toInt)
    case _ => throw new Exception("Usage: awaitClosed <port>")
  }
}
