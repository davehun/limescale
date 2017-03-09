package example
import akka.stream.ActorMaterializer
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import play.api.libs.json.JsValue
import java.io._

object Hello {

  def main(args: Array[String]): Unit = {
    if (args.length == 0) {
     println("Argument required")
     return
    }

    val page: String = args(0)
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    val wsClient = StandaloneAhcWSClient()

    call(wsClient,page)
      .andThen { case _ => wsClient.close() }
      .andThen { case _ => system.terminate() }
  }

  def call(wsClient: StandaloneAhcWSClient, page: String): Future[Unit] = {

    wsClient.url(s"https://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=$page").get().map
    { response =>
      val statusText: String = response.statusText
      val bodyText: String = response.body
      val asJson: JsValue = response.json
      //'query":{"normalized":[{"from":"java","to":"Java"}],"pages":{"69336":{"pageid":69336,"ns":0,"title":"Java","revisions":[{"contentformat":"text/x-wiki","contentmodel":"wikitext","*":"'
      processJson(asJson)
      //println(s"Got a response $statusText value $bodyText")
      saveFile(bodyText)
    }
  }

  def processJson(asJson: JsValue){

      for (thing <- asJson\\"*") {
        //longest(thing.asString())
        //mostCommon(thing)
        println(thing.toString())
      }
  }

  def saveFile(content: String) = {
    val writer = new PrintWriter(new File("test.txt" ))
    writer.write(content)
    writer.close()
  }
}
