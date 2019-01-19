import java.util.UUID

import io.estatico.newtype.macros.newtype

object NewTypeUsage {
  @newtype case class OrderId(value:UUID){
    def text: String = s"ORDER:$value"
  }
}
