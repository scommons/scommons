package scommons.components

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("./scommons/components/my-module", JSImport.Namespace)
@js.native
object MyModule extends js.Object {

  def multiplyByTwo(x: Int): Int = js.native
}
