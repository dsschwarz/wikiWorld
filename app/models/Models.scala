package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._


case class Node(id: Long, url: String)
case class Link(nodeA: Long, nodeB: Long)
case class CustomException(msg:String)  extends Exception

object Node {
	val node = {
		get[Long]("id") ~ 
		get[String]("url") map {
			case id~url => Node(id, url)
		}
	}

	def all(): List[Node] = DB.withConnection { implicit c =>
	  SQL("select * from node").as(node *)
	}

	def find(selector: String): List[Node] = DB.withConnection { implicit c =>
		SQL(selector).as(node *)
	}

	def findOrCreate(url: String) : Node = {
		var nodes = Node.find("select * from node where url='" + url+"'") //dw bout sql injection
		if (nodes.isEmpty) {
			Node.create(url)
			nodes = Node.find("select * from node where url='" + url+"'")
		}
		return nodes.head
	}
	
	def create(url: String) {
		DB.withConnection { implicit c =>
			SQL("insert into node (url) values ({url})").on(
				'url -> url
				).executeUpdate()
		}
	}

	def delete(id: Long) {
		DB.withConnection { implicit c =>
			SQL("delete from node where id = {id}").on(
				'id -> id
				).executeUpdate()
		}
	}
}

object Link {
	val linkParser = {
		get[Long]("nodeA") ~ 
		get[Long]("nodeB") map {
			case nodeA~nodeB => Link(nodeA, nodeB)
		}
	}

	def find(selector: String): List[Link] = DB.withConnection { implicit c =>
		SQL(selector).as(linkParser *)
	}

	def create(nodeA: Long, nodeB: Long) {
		DB.withConnection { implicit c =>
			SQL("insert into link (nodeA, nodeB) values ({nodeA}, {nodeB})").on(
				'nodeA -> nodeA,
				'nodeB -> nodeB
				).executeUpdate()
		}
	}
}