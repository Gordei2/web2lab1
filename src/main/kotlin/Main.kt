data class Contact(
    var name: String,
    var phoneNumber: String
)

class MobilePhone(val myNumber: String, private val myContacts: Collection<Contact>) {

    fun addNewContact(contact: Contact) = myContacts.toMutableSet().add(contact)

    fun updateContact(old: Contact, new: Contact): Boolean {
        val contact = myContacts.firstOrNull { it.name == old.name } ?: return false

        contact.name = new.name
        contact.phoneNumber = new.phoneNumber

        return true
    }

    fun removeContact(contact: Contact) = myContacts.toMutableList().remove(contact)

    fun queryContact(name: String) = myContacts.firstOrNull { it.name == name }

    fun printContacts() = myContacts.forEach(::println)
}

class Node(
    var key: Int,
    var left: Node? = null,
    var right: Node? = null) {

    fun find(value: Int): Node? = when {
        this.key > value -> left?.find(value)
        this.key < value -> right?.find(value)
        else -> this

    }

    fun insert(value: Int) {
        if (value > this.key) {
            if (this.right == null) {
                this.right = Node(value)
            } else {
                this.right?.insert(value)
            }
        } else if (value < this.key) {
            if (this.left == null) {
                this.left = Node(value)
            } else {
                this.left?.insert(value)
            }
        }
    }

    fun delete(value: Int) {
        when {
            value > key -> scan(value, this.right, this)
            value < key -> scan(value, this.left, this)
            else -> removeNode(this, null)
        }
    }

    private fun scan(value: Int, node: Node?, parent: Node?) {
        if (node == null) {
            System.out.println("value " + value
                    + " seems not present in the tree.")
            return
        }
        when {
            value > node.key -> scan(value, node.right, node)
            value < node.key -> scan(value, node.left, node)
            value == node.key -> removeNode(node, parent)
        }

    }

    private fun removeNode(node: Node, parent: Node?) {
        node.left?.let { leftChild ->
            run {
                node.right?.let {
                    removeTwoChildNode(node)
                } ?: removeSingleChildNode(node, leftChild)
            }
        } ?: run {
            node.right?.let { rightChild -> removeSingleChildNode(node, rightChild) } ?: removeNoChildNode(node, parent)
        }


    }

    private fun removeNoChildNode(node: Node, parent: Node?) {
        parent?.let { p ->
            if (node == p.left) {
                p.left = null
            } else if (node == p.right) {
                p.right = null
            }
        } ?: throw IllegalStateException(
            "Can not remove the root node without child nodes")

    }

    private fun removeTwoChildNode(node: Node) {
        val leftChild = node.left!!
        leftChild.right?.let {
            val maxParent = findParentOfMaxChild(leftChild)
            maxParent.right?.let {
                node.key = it.key
                maxParent.right = null
            } ?: throw IllegalStateException("Node with max child must have the right child!")

        } ?: run {
            node.key = leftChild.key
            node.left = leftChild.left
        }

    }

    private fun findParentOfMaxChild(n: Node): Node {
        return n.right?.let { r -> r.right?.let { findParentOfMaxChild(r) } ?: n }
            ?: throw IllegalArgumentException("Right child must be non-null")

    }

    private fun removeSingleChildNode(parent: Node, child: Node) {
        parent.key = child.key
        parent.left = child.left
        parent.right = child.right
    }

    fun visit(): Array<Int> {
        val a = left?.visit() ?: emptyArray()
        val b = right?.visit() ?: emptyArray()
        return a + arrayOf(key) + b
    }
}

fun main(args: Array<String>) {
    val tree = Node(4)
    val keys = arrayOf(8, 15, 21, 3, 7, 2, 5, 10, 2, 3, 4, 6, 11)
    for (key in keys) {
        tree.insert(key)
    }
    val node = tree.find(4)!!
    println("Node with value ${node.key} [left = ${node.left?.key}, right = ${node.right?.key}]")
    println("Delete node with key = 3")
    node.delete(3)
    print("Tree content after the node elimination: ")
    println(tree.visit().joinToString { it.toString() })
}