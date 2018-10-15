package scommons.client.ui.list

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import scommons.client.ui.UiComponent

case class PickListProps(items: List[ListBoxData],
                         selectedIds: Set[String] = Set.empty,
                         preSelectedIds: Set[String] = Set.empty,
                         onSelect: Set[String] => Unit = _ => (),
                         sourceTitle: String = "Available items:",
                         destTitle: String = "Selected items:")

object PickList extends UiComponent[PickListProps] {

  private case class PickListState(selectedIds: Set[String],
                                   selectedSourceIds: Set[String] = Set.empty,
                                   selectedDestIds: Set[String] = Set.empty)

  def apply(): ReactClass = reactClass
  lazy val reactClass: ReactClass = createComp

  private def createComp = React.createClass[PropsType, PickListState](
    getInitialState = { self =>
      val props = self.props.wrapped
      
      PickListState(props.selectedIds ++ props.preSelectedIds)
    },
    componentWillReceiveProps = { (self, nextProps) =>
      val prevProps = self.props.wrapped
      val props = nextProps.wrapped
      if (prevProps.selectedIds != props.selectedIds ||
        prevProps.preSelectedIds != props.preSelectedIds) {
        self.setState(_.copy(selectedIds = props.selectedIds ++ props.preSelectedIds))
      }
    },
    render = { self =>
      val props = self.props.wrapped
      val state = self.state

      val sourceItems = props.items.filterNot(i => state.selectedIds.contains(i.id))
      val destItems = props.items.filter(i => state.selectedIds.contains(i.id))
      val removeIds = state.selectedDestIds -- props.preSelectedIds
      val removeAllIds = state.selectedIds -- props.preSelectedIds
      val selectAllIds = sourceItems.map(_.id).toSet

      def handleAdd(ids: Set[String]): Unit = self.setState(s => s.copy(
        selectedIds = s.selectedIds ++ ids,
        selectedSourceIds = s.selectedSourceIds -- ids
      ))

      def handleRemove(ids: Set[String]): Unit = self.setState(s => s.copy(
        selectedIds = s.selectedIds -- ids,
        selectedDestIds = s.selectedDestIds -- ids
      ))

      <.div(^.className := "row-fluid")(
        <.div(^.className := "span5")(
          <.strong()(props.sourceTitle),
          <.hr(^.style := Map("margin" -> "7px 0"))(),

          <(ListBox())(^.wrapped := ListBoxProps(
            items = sourceItems,
            selectedIds = state.selectedSourceIds,
            onSelect = { ids => self.setState(s => s.copy(selectedSourceIds = ids)) }
          ))()
        ),

        <(PickButtons())(^.wrapped := PickButtonsProps(
          className = Some("span2"),
          addEnabled = state.selectedSourceIds.nonEmpty,
          removeEnabled = removeIds.nonEmpty,
          addAllEnabled = selectAllIds.nonEmpty,
          removeAllEnabled = removeAllIds.nonEmpty,
          onAdd = { () => handleAdd(state.selectedSourceIds) },
          onRemove = { () => handleRemove(removeIds) },
          onAddAll = { () => handleAdd(selectAllIds) },
          onRemoveAll = { () => handleRemove(removeAllIds) }
        ))(),

        <.div(^.className := "span5")(
          <.strong()(props.destTitle),
          <.hr(^.style := Map("margin" -> "7px 0"))(),

          <(ListBox())(^.wrapped := ListBoxProps(
            items = destItems,
            selectedIds = state.selectedDestIds,
            onSelect = { ids => self.setState(s => s.copy(selectedDestIds = ids)) }
          ))()
        )
      )
    }
  )
}