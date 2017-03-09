package example
import akka.stream.ActorMaterializer
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import play.api.libs.json.JsValue
object Hello {

  def main(args: Array[String]): Unit = {
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
      println(s"Got a response $statusText value $bodyText")
    }
  }


}
