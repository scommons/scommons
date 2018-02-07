package scommons.client.ui.tree

import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import scommons.client.TestSpec
import scommons.client.ui.tree.BrowseTreeCss._
import scommons.client.util.BrowsePath

class BrowseTreeSpec extends TestSpec {

  it should "call onSelect function when select new item" in {
    //given
    val onSelect = mockFunction[BrowseTreeData, Unit]
    val data = BrowseTreeItemData("test", BrowsePath("/test"))
    val props = BrowseTreeProps(List(data), onSelect = onSelect)
    val comp = shallowRender(<(BrowseTree())(^.wrapped := props)())
    val nodeProps = findComponentProps(comp, BrowseTreeNode)

    //then
    onSelect.expects(data)

    //when
    nodeProps.onSelect(data)
  }

  it should "not call onSelect function when select the same item" in {
    //given
    val onSelect = mockFunction[BrowseTreeData, Unit]
    val data = BrowseTreeItemData("test", BrowsePath("/test"))
    val props = BrowseTreeProps(List(data), selectedItem = Some(data.path), onSelect = onSelect)
    val comp = shallowRender(<(BrowseTree())(^.wrapped := props)())
    val nodeProps = findComponentProps(comp, BrowseTreeNode)
    nodeProps.selected shouldBe true

    //then
    onSelect.expects(_: BrowseTreeData).never()

    //when
    nodeProps.onSelect(data)
  }

  it should "expand node when onExpand" in {
    //given
    val data = BrowseTreeItemData("test", BrowsePath("/test"))
    val props = BrowseTreeProps(List(data))
    val renderer = createRenderer()
    renderer.render(<(BrowseTree())(^.wrapped := props)())
    val comp = renderer.getRenderOutput()
    val nodeProps = findComponentProps(comp, BrowseTreeNode)
    nodeProps.expanded shouldBe false

    //when
    nodeProps.onExpand(data)

    //then
    val updatedComp = renderer.getRenderOutput()
    findComponentProps(updatedComp, BrowseTreeNode).expanded shouldBe true
  }

  it should "collapse initially opened node when onExpand" in {
    //given
    val data = BrowseTreeItemData("test", BrowsePath("/test"))
    val props = BrowseTreeProps(List(data), initiallyOpenedNodes = Set(data.path))
    val renderer = createRenderer()
    renderer.render(<(BrowseTree())(^.wrapped := props)())
    val comp = renderer.getRenderOutput()
    val nodeProps = findComponentProps(comp, BrowseTreeNode)
    nodeProps.expanded shouldBe true

    //when
    nodeProps.onExpand(data)

    //then
    val updatedComp = renderer.getRenderOutput()
    findComponentProps(updatedComp, BrowseTreeNode).expanded shouldBe false
  }

  it should "collapse node when onExpand again" in {
    //given
    val data = BrowseTreeItemData("test", BrowsePath("/test"))
    val props = BrowseTreeProps(List(data))
    val renderer = createRenderer()
    renderer.render(<(BrowseTree())(^.wrapped := props)())
    val comp = renderer.getRenderOutput()
    val nodeProps = findComponentProps(comp, BrowseTreeNode)
    nodeProps.expanded shouldBe false
    nodeProps.onExpand(data)
    val nodePropsV2 = findComponentProps(renderer.getRenderOutput(), BrowseTreeNode)
    nodePropsV2.expanded shouldBe true

    //when
    nodePropsV2.onExpand(data)

    //then
    val nodePropsV3 = findComponentProps(renderer.getRenderOutput(), BrowseTreeNode)
    nodePropsV3.expanded shouldBe false
  }

  it should "render selected node" in {
    //given
    val data = BrowseTreeItemData("test", BrowsePath("/test"))
    val props = BrowseTreeProps(List(data), selectedItem = Some(data.path))
    val component = <(BrowseTree())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    findComponentProps(result, BrowseTreeNode).selected shouldBe true
  }

  it should "render opened node" in {
    //given
    val node = BrowseTreeNodeData("test", BrowsePath("/test"))
    val props = BrowseTreeProps(List(node), openedNodes = Set(node.path))
    val component = <(BrowseTree())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    findComponentProps(result, BrowseTreeNode).expanded shouldBe true
  }

  it should "render initially opened node" in {
    //given
    val node1 = BrowseTreeNodeData("node 1", BrowsePath("/node-1"))
    val node2 = BrowseTreeNodeData("node 2", BrowsePath("/node-2"))
    val props = BrowseTreeProps(List(node1, node2),
      openedNodes = Set(node1.path),
      initiallyOpenedNodes = Set(node2.path))
    val component = <(BrowseTree())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    findProps(result, BrowseTreeNode).map(n => (n.data.path, n.expanded)) shouldBe List(
      (node1.path, true),
      (node2.path, true)
    )
  }

  it should "render opened node when componentWillReceiveProps" in {
    //given
    val node1 = BrowseTreeNodeData("node 1", BrowsePath("/node-1"))
    val prevProps = BrowseTreeProps(List(node1), openedNodes = Set(node1.path))
    val renderer = createRenderer()
    renderer.render(<(BrowseTree())(^.wrapped := prevProps)())
    val comp = renderer.getRenderOutput()
    findComponentProps(comp, BrowseTreeNode).expanded shouldBe true
    val node2 = BrowseTreeNodeData("node 2", BrowsePath("/node-2"))
    val props = BrowseTreeProps(List(node1, node2), openedNodes = Set(node2.path))

    //when
    renderer.render(<(BrowseTree())(^.wrapped := props)())

    //then
    findProps(renderer.getRenderOutput(), BrowseTreeNode).map(n => (n.data.path, n.expanded)) shouldBe List(
      (node1.path, true),
      (node2.path, true)
    )
  }

  it should "not render opened node when it was removed" in {
    //given
    val renderer = createRenderer()
    val node1 = BrowseTreeNodeData("node 1", BrowsePath("/node-1"))
    val props = BrowseTreeProps(List(node1), openedNodes = Set(node1.path))
    renderer.render(<(BrowseTree())(^.wrapped := props)())
    findComponentProps(renderer.getRenderOutput(), BrowseTreeNode).expanded shouldBe true
    val node2 = BrowseTreeNodeData("node 2", BrowsePath("/node-2"))
    val propsV2 = BrowseTreeProps(List(node2), openedNodes = Set(node2.path))
    renderer.render(<(BrowseTree())(^.wrapped := propsV2)())
    findComponentProps(renderer.getRenderOutput(), BrowseTreeNode).expanded shouldBe true
    val propsV3 = BrowseTreeProps(List(node1, node2))

    //when
    renderer.render(<(BrowseTree())(^.wrapped := propsV3)())

    //then
    findProps(renderer.getRenderOutput(), BrowseTreeNode).map(n => (n.data.path, n.expanded)) shouldBe List(
      (node1.path, false),
      (node2.path, true)
    )
  }

  it should "render child nodes" in {
    //given
    val topItem = BrowseTreeItemData("top item", BrowsePath("/top-item"))
    val childItem = BrowseTreeItemData("child item", BrowsePath("/child-item"))
    val childNode = BrowseTreeNodeData("child node", BrowsePath("/child-node"), children = List(childItem))
    val topNode = BrowseTreeNodeData("top node", BrowsePath("/top-node"), children = List(childNode))
    val props = BrowseTreeProps(List(topItem, topNode), openedNodes = Set(topNode.path, childNode.path))
    val component = <(BrowseTree())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    assertDOMComponent(result, <.div(^.className := browseTree)(), { case List(topItemE, topNodeE) =>
      assertComponent(topItemE, BrowseTreeNode(), { topItemProps: BrowseTreeNodeProps =>
        inside(topItemProps) { case BrowseTreeNodeProps(data, level, selected, _, expanded, _) =>
          data shouldBe topItem
          level shouldBe 0
          selected shouldBe false
          expanded shouldBe false
        }
      })
      assertComponent(topNodeE, BrowseTreeNode(), { topNodeProps: BrowseTreeNodeProps =>
        inside(topNodeProps) { case BrowseTreeNodeProps(data, level, selected, _, expanded, _) =>
          data shouldBe topNode
          level shouldBe 0
          selected shouldBe false
          expanded shouldBe true
        }
      }, { case List(childNodeE) =>
        assertComponent(childNodeE, BrowseTreeNode(), { childNodeProps: BrowseTreeNodeProps =>
          inside(childNodeProps) { case BrowseTreeNodeProps(data, level, selected, _, expanded, _) =>
            data shouldBe childNode
            level shouldBe 1
            selected shouldBe false
            expanded shouldBe true
          }
        }, { case List(childItemE) =>
          assertComponent(childItemE, BrowseTreeNode(), { childItemProps: BrowseTreeNodeProps =>
            inside(childItemProps) { case BrowseTreeNodeProps(data, level, selected, _, expanded, _) =>
              data shouldBe childItem
              level shouldBe 2
              selected shouldBe false
              expanded shouldBe false
            }
          })
        })
      })
    })
  }

  it should "return all nodes paths when getAllPaths" in {
    //given
    val item1 = BrowseTreeItemData("item1", BrowsePath("/item1"))
    val item2 = BrowseTreeItemData("item2", BrowsePath("/item2"))
    val item3 = BrowseTreeItemData("item3", BrowsePath("/item3"))
    val item4 = BrowseTreeItemData("item4", BrowsePath("/item4"))
    val node2 = BrowseTreeNodeData("node2", BrowsePath("/node2"))
    val node4 = BrowseTreeNodeData("node4", BrowsePath("/node4"))
    val node3 = BrowseTreeNodeData("node3", BrowsePath("/node3"), children = List(item4, node4))
    val node1 = BrowseTreeNodeData("node1", BrowsePath("/node1"), children = List(item3, node2, node3))
    val item5 = BrowseTreeItemData("item5", BrowsePath("/item5"))
    val roots = List(item1, item2, node1, item5)

    //when
    val result = BrowseTree.getAllPaths(roots)

    //then
    result shouldBe Set(
      item1.path, item2.path, node1.path, item5.path, item4.path, node4.path, item3.path, node2.path, node3.path
    )
  }
}
