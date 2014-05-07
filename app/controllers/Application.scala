package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

	def index = Action {
		Ok(views.html.index("Your new application is ready."))
	}

	def scrape(url: String) = {
		val webPage: Future[libs.ws.Response] = WS.url(url).get().getBody()
		val stringRegex = """<a\s+(?:[^>]*\s+)?href="([^"]*)"[^<]*</a>""""
		val linkPattern = stringRegex.r
		val links = linkPattern findAllIn webPage

	}

}