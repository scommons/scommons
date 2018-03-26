package scommons.client.ui.table

import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import org.scalatest.Succeeded
import scommons.client.test.TestSpec
import scommons.client.test.raw.ReactTestUtils
import scommons.client.test.raw.ReactTestUtils._
import scommons.client.test.raw.ShallowRenderer.ComponentInstance
import scommons.client.ui.table.TablePanelCss._

class TablePanelSpec extends TestSpec {

  it should "call onSelect once and select row when click on row" in {
    //given
    val onSelect = mockFunction[TableRowData, Unit]
    val props = TablePanelProps(List(
      TableColumnData("Col1"),
      TableColumnData("Col2")
    ), List(
      TableRowData("1", List("Cell1.1", "Cell1.2")),
      TableRowData("2", List("Cell2.1", "Cell2.2"))
    ), onSelect = onSelect)
    val comp = renderIntoDocument(<(TablePanel())(^.wrapped := props)())
    val rows = scryRenderedDOMComponentsWithClass(comp, tablePanelRow)
    rows.length shouldBe props.rows.size
    val nextSelectIndex = 0

    //then
    onSelect.expects(props.rows(nextSelectIndex)).once()

    //when & then
    ReactTestUtils.Simulate.click(rows(nextSelectIndex))
    rows(nextSelectIndex).className shouldBe tablePanelSelectedRow

    //when & then
    ReactTestUtils.Simulate.click(rows(nextSelectIndex))
    rows(nextSelectIndex).className shouldBe tablePanelSelectedRow
  }

  it should "reset selectedIds when componentWillReceiveProps" in {
    //given
    val prevProps = TablePanelProps(List(
      TableColumnData("Col1"),
      TableColumnData("Col2")
    ), List(
      TableRowData("1", List("Cell1.1", "Cell1.2")),
      TableRowData("2", List("Cell2.1", "Cell2.2"))
    ))
    val renderer = createRenderer()
    renderer.render(<(TablePanel())(^.wrapped := prevProps)())
    val comp = renderer.getRenderOutput()
    assertTablePanel(comp, prevProps)

    val props = prevProps.copy(selectedIds = Set("1"))

    //when
    renderer.render(<(TablePanel())(^.wrapped := props)())

    //then
    val compV2 = renderer.getRenderOutput()
    assertTablePanel(compV2, props)
  }

  it should "render component" in {
    //given
    val props = TablePanelProps(List(
      TableColumnData("Col1"),
      TableColumnData("Col2")
    ), List(
      TableRowData("1", List("Cell1.1", "Cell1.2")),
      TableRowData("2", List("Cell2.1", "Cell2.2"))
    ))
    val comp = <(TablePanel())(^.wrapped := props)()

    //when
    val result = shallowRender(comp)

    //then
    assertTablePanel(result, props)
  }

  it should "render component with pre-selected row" in {
    //given
    val props = TablePanelProps(
      List(
        TableColumnData("Col1"),
        TableColumnData("Col2")
      ),
      List(
        TableRowData("1", List("Cell1.1", "Cell1.2")),
        TableRowData("2", List("Cell2.1", "Cell2.2"))
      ),
      selectedIds = Set("1")
    )
    val comp = <(TablePanel())(^.wrapped := props)()

    //when
    val result = shallowRender(comp)

    //then
    assertTablePanel(result, props)
  }

  private def assertTablePanel(result: ComponentInstance, props: TablePanelProps): Unit = {
    val expectedHeader = props.header.map { column =>
      <.th(^("colSpan") := "1")(column.title)
    }

    val expectedRows = props.rows.map { row =>
      val rowClass =
        if (props.selectedIds.contains(row.id)) tablePanelSelectedRow
        else tablePanelRow

      <.tr(^.className := rowClass)() -> row.cells.map { cell =>
        <.td()(cell)
      }
    }

    assertDOMComponent(result, <.table(
      ^.className := "table table-condensed",
      ^("cellSpacing") := "0"
    )(), { case List(thead, tbody) =>
      assertDOMComponent(thead, <.thead(^("aria-hidden") := "false")(), { case List(tr) =>
        assertDOMComponent(tr, <.tr()(), { headerRow =>
          headerRow.size shouldBe expectedHeader.size
          headerRow.zip(expectedHeader).foreach { case (headerCell, expectedElem) =>
            assertDOMComponent(headerCell, expectedElem)
          }

          Succeeded
        })
      })
      assertDOMComponent(tbody, <.tbody()(), { rows =>
        rows.size shouldBe expectedRows.size
        rows.zip(expectedRows).foreach { case (resultRow, (expectedRowElem, expectedCells)) =>
          assertDOMComponent(resultRow, expectedRowElem, { resultCells =>
            resultCells.size shouldBe expectedCells.size
            resultCells.zip(expectedCells).foreach { case (resultCell, expectedCellElem) =>
              assertDOMComponent(resultCell, expectedCellElem)
            }

            Succeeded
          })
        }

        Succeeded
      })
    })
  }
}
