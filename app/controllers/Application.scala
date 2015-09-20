package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws._
import play.api.libs.json.Json

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

import models._

object Application extends Controller {
	def index = Action {
		Ok(views.html.index())
	}

	def links = Action {
		val links = Link.find("select * from link")
		val asMap = links.map {l =>
			val nodeA = l.nodeA
			val nodeB = l.nodeB
			Map(
			"start" -> Node.find(s"select * from node where id=$nodeA").head.url,
			"end" -> Node.find(s"select * from node where id=$nodeB").head.url
		) }
		Ok(views.html.links(asMap))
	}

	def retrieveLinks(url: String) : List[Map[String, String]] = {
		val nodes = Node.find("select * from node where url='" + url+"'") //dw bout sql injection
		if (nodes.isEmpty) {
			scrape(url)
			throw new CustomException("fetchingLinks")
		} else {
			val node = nodes.head
			val links = Link.find("select * from link where nodeA = " + node.id + " OR nodeB = " + node.id)
			links.map {l => Map(
				"start" -> (if (l.nodeA == node.id) node.url else Node.find("select * from node where id=" + l.nodeA).head.url ),
				"end" -> (if (l.nodeB == node.id) node.url else Node.find("select * from node where id=" + l.nodeB).head.url )
			)}
		}
	}

	def getNode(url: Option[String]) = Action { implicit request =>
		// val links = retrieveLinks("http://workwithplay.com/blog/2013/05/08/persist-data-with-anorm/")
		url match {
			case Some(name) =>
				try {
					val links = retrieveLinks(name)
					Ok(Json.toJson(links))
				} catch {
					case ex: CustomException => {
						Ok(Json.toJson( Map("success"->"false", "message" -> "Getting links")) )
					}
				}
			case None => Ok(Json.toJson( Map("success"->"false", "message" -> "Need Url")) )
		}
	}

	def scrape(url: String) = {
		val webPage = WS.url(url).get
		webPage onComplete {
			case Success(page) => {
				val linkPattern = """<a\s+(?:[^>]*\s+)?href="(http[^:]*:[^"]*)"[^<]*</a>""".r
				val links = linkPattern.findAllMatchIn( page.body ).toArray.map({ m => m.group(1)}).distinct
				for (link <- links) { println(link) }

				val node = Node.findOrCreate(url)
				for (link <- links) {
					Link.create(node.id, Node.findOrCreate(link).id)
				}
				//Save to db
			}
			case Failure(t) => println("An error has occurred: " + t.getMessage)
		}
	}

}