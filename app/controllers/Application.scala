package controllers

import play.api._
import play.api.mvc._

import play.

object Application extends Controller {

	def index = Action {
		Ok(views.html.index("Your new application is ready."))
	}

	def scrape(url: String) = {
		val webPage: Future[libs.ws.Response] = WS.url(url).get().getBody()
		Async {

			val linkPattern = """<a\s+(?:[^>]*\s+)?href="([^"]*)"[^<]*</a>""".r
			val links = linkPattern.findAllMatchIn(webpage).toArray.map({ m => m.group(1)})
		}

	}

}