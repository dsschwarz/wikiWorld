package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws._

import scala.concurrent.Future
import scala.util.{Success, Failure}

import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

	def index = Action {
		scrape("http://docs.scala-lang.org/overviews/core/futures.html")
		Ok(views.html.index("Your new application is ready."))
	}

	def scrape(url: String) = {
		val webPage = WS.url(url).get
		webPage onComplete {
			case Success(page) => {
				val linkPattern = """<a\s+(?:[^>]*\s+)?href="(http[^:]*:[^"]*)"[^<]*</a>""".r
				val links = linkPattern.findAllMatchIn( page.body ).toArray.map({ m => m.group(1)})
				for (link <- links) { println(link) }
			}
			case Failure(t) => println("An error has occurred: " + t.getMessage)
		}

	}

}