package scommons.client.ui

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.events.{FormSyntheticEvent, KeyboardSyntheticEvent}
import org.scalajs.dom.raw.HTMLInputElement
import scommons.client.util.KeyCodes

case class TextFieldProps(text: String,
                          onChange: (String) => Unit,
                          requestFocus: Boolean = false,
                          requestSelect: Boolean = false,
                          className: Option[String],
                          placeholder: Option[String] = None,
                          onEnter: () => Unit = () => ())

object TextField {

  private case class TextFieldState(setInputRef: HTMLInputElement => Unit,
                                    getInputRef: () => HTMLInputElement)

  def apply(): ReactClass = reactClass

  private lazy val reactClass = React.createClass[TextFieldProps, TextFieldState](
    getInitialState = { _ =>
      var inputRef: HTMLInputElement = null

      TextFieldState({ ref =>
        inputRef = ref
      }, { () =>
        inputRef
      })
    },
    componentDidUpdate = { (self, prevProps, _) =>
      val inputRef = self.state.getInputRef()
      val value = inputRef.value
      if (self.props.wrapped.requestSelect
        && self.props.wrapped.requestSelect != prevProps.wrapped.requestSelect
        && value.nonEmpty) {

        inputRef.setSelectionRange(0, value.length)
      }

      if (self.props.wrapped.requestFocus
        && self.props.wrapped.requestFocus != prevProps.wrapped.requestFocus) {

        inputRef.focus()
      }
    },
    render = { self =>
      val props = self.props.wrapped

      <.input(
        ^.`type` := "text",
        props.className.map { className =>
          ^.className := className
        },
        ^.value := props.text,
        props.placeholder.map { placeholder =>
          ^.placeholder := placeholder
        },
        ^.ref := { ref: HTMLInputElement =>
          self.state.setInputRef(ref)
        },
        ^.onChange := { e: FormSyntheticEvent[HTMLInputElement] =>
          val value = e.target.value
          props.onChange(value)
        },
        ^.onKeyDown := { e: KeyboardSyntheticEvent =>
          if (e.keyCode == KeyCodes.KEY_ENTER) {
            props.onEnter()
          }
        }
      )()
    }
  )
}
