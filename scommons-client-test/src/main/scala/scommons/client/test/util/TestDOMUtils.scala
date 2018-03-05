package scommons.client.test.util

import io.github.shogowada.statictags
import org.scalajs.dom
import org.scalajs.dom._
import org.scalatest.Matchers
import scommons.client.test.raw.TestReactDOM

import scala.scalajs.js

object TestDOMUtils extends Matchers {

  def createDomEvent[T <: Event](args: js.Any*)(implicit tag: js.ConstructorTag[T]): T = {
    js.Dynamic.newInstance(tag.constructor)(args: _*).asInstanceOf[T]
  }

  def findReactElement(component: js.Any): dom.Element = asElement(TestReactDOM.findDOMNode(component))

  private def asElement(node: Node): dom.Element = node.asInstanceOf[dom.Element]

  def assertDOMElement(result: dom.Element, expected: statictags.Element): Unit = {
    assertElement(TestDOMPath(result, result), expected)
  }

  private def assertElement(path: TestDOMPath, expected: statictags.Element): Unit = {
    val node = path.currNode
    node.nodeName.toLowerCase shouldBe expected.name.toLowerCase

    assertAttributes(
      path,
      getAttributes(node).toMap,
      expected.flattenedAttributes.map(a => (a.name, a.valueToString)).toMap
    )

    val nodes = expected.flattenedContents.toList
    assertChildNodes(
      path,
      getChildNodes(node),
      nodes.map {
        case node: statictags.Element => node.name.toLowerCase
        case _ => "#text"
      },
      assertElement = { (path: TestDOMPath, index: Int) =>
        nodes(index) match {
          case node: statictags.Element =>
            assertElement(path, node)
          case textNode =>
            assertTextContent(path, textNode.toString)
        }
      }
    )
  }

  private def assertClasses(path: TestDOMPath,
                            resultClasses: String,
                            expectedClasses: String): Unit = {

    if (resultClasses != expectedClasses) {
      def normalize(classes: String) = classes.split(' ').map(_.trim).filter(_.nonEmpty)

      val result = normalize(resultClasses)
      val expected = normalize(expectedClasses)

      if (result.toSet != expected.toSet) {
        fail(
          s"""$path  <-- classes don't match
             |got:
             |\t${result.sorted.mkString(" ")}
             |expected:
             |\t${expected.sorted.mkString(" ")}
             |""".stripMargin)
      }
    }
  }

  private def assertAttributes(path: TestDOMPath,
                               resultAttrs: Map[String, String],
                               expectedAttrs: Map[String, String]): Unit = {

    val result = resultAttrs - "data-reactroot"
    val expected = expectedAttrs - "data-reactroot"

    def mkString(attrs: Map[String, String]): String = {
      attrs.toList.sorted.map(p => s"${p._1}=${p._2}").mkString("\n\t")
    }

    val resultKeys = result.keySet
    val expectedKeys = expected.keySet

    if (resultKeys != expectedKeys) {
      fail(
        s"""$path  <-- attribute keys don't match
           |got:
           |\t${mkString(result)}
           |expected:
           |\t${mkString(expected)}
           |""".stripMargin)
    }

    for ((expectedKey, expectedVal) <- expected) {
      val resultVal = result(expectedKey)

      if (expectedKey == "class") {
        assertClasses(path, resultVal, expectedVal)
      }
      else if (resultVal != expectedVal) {
        fail(
          s"""$path  <-- attribute value don't match
             |got:
             |\t$expectedKey = [$resultVal]
             |expected:
             |\t$expectedKey = [$expectedVal]
             |""".stripMargin)
      }
    }
  }

  private def assertChildNodes(path: TestDOMPath,
                               childList: List[Node],
                               expectedTags: List[String],
                               assertElement: (TestDOMPath, Int) => Unit): Unit = {

    val result = childList.filter(_.nodeName != "#comment")

    val resultTags = result.map(_.nodeName.toLowerCase)
    if (resultTags != expectedTags) {
      fail(
        s"""$path  <-- child tags don't match
           |got:
           |\t${resultTags.mkString("<", ">\n\t<", ">")}
           |expected:
           |\t${expectedTags.mkString("<", ">\n\t<", ">")}
           |""".stripMargin)
    }

    for (i <- result.indices) {
      assertElement(path.at(asElement(result(i))), i)
    }
  }

  private def assertTextContent(path: TestDOMPath, expectedText: String): Unit = {
    val resultText = path.currNode.textContent

    if (resultText != expectedText) {
      fail(
        s"""$path  <-- text doesn't match
           |got:
           |\t[$resultText]
           |expected:
           |\t[$expectedText]
           |""".stripMargin)
    }
  }

  private def getChildNodes(node: Node): List[Node] = {
    val childNodes = node.childNodes

    if (js.isUndefined(childNodes) || childNodes.length == 0) List.empty[Node]
    else {
      var result = List.empty[Node]
      for (i <- (0 until childNodes.length).reverse) {
        result = childNodes(i) :: result
      }

      result
    }
  }

  private def getAttributes(node: Node): List[(String, String)] = {
    val nodeMap = node.attributes

    if (nodeMap.length == 0) List.empty
    else {
      var result = List.empty[(String, String)]
      for (i <- 0 until nodeMap.length) {
        val attr = nodeMap.item(i)
        result = (attr.name, attr.value) :: result
      }

      result
    }
  }
}
