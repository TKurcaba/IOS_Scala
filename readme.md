# The task - paths in Imaginary Object Syntax

Let's assume we are working with the Imaginary Object Syntax (IOS for short) notation that looks as follow:

```
a = [
  prop1 = "first entry of a"
  prop2 = "second entry of a"
]
b = [
  prop1 = "first entry of b"
  prop2 = "second entry of b"
  nested = [
    whatsthat = "object nested in b"
  ]
]
```

This describes two objects, named `a` and `b`. Both of them have entries `prop1` and `prop2`. Additionally, object `b` contains a nested object in a property named `nested`. The formal definition of format can be found near the bottom of this document.

Your task is to write a simple program that allows querying IOS files for its content. Our queries are lists of keys separated by dots `.` specifying a path to the entry in the IOS file.

For example, for the file above:

- query `a.prop1` should return `"first entry of a"`
- query `b.nested.whatsthat` should return `"object nested in b"`
- query `a.nested.whatsthat` shouldn't find anything

Your program should accept the path for the IOS file and query as it's command-line arguments and print on standard output what was found for the input query:

- `None` means that nothing was found
- `Object` means that object was found. You don't need to print any additional information about the object.
- `Value(<content>)` means that string value was found. `<content>` should be replaced with the found string.

You can asume that file provided as a first argument is correct and well-structured, so you do not need to handle any errors regarding IOS format.

## Formal definition of IOS format

- IOS file consists of a list of `entries`
- `entry` consist of a `key`, `=` sign, and a `value`
- `key` is a string containing only lowercase letters (`a-z`)
- `value` is either string surrounded by quotation marks (`" "`) or `object`
- `object` consist of a list of `entries` surrounded by brackets (`[ ]`)
- all whitespaces (including newlines) outside of quotation marks are ignored

## Examples

Assuming we have following file named `data.ios`:

```
crate = [
  material = "wood"
  content = [
    item = [
      name = "jar"
      content = "strawberry jam"
    ]
    count = "8"
  ]
  description =
    "wooden crate with jars full of strawberry jam"
]

bag = [
  color="black"content=[items="books"count="3"about="scala"]space="very little so I'm not using spaces to fit"
]

backpack = [ empty="true" ]
bin = [

]
```

and you are using scala-cli, your program behavior should be the same as in the following table:

| command                                             |         standard output |
| :---                                                |                    ---: |
| scala-cli . -- data.ios crate    <br/>                   |                  Object |
| scala-cli . -- data.ios grate                       |                    None |
| scala-cli . -- data.ios crate.material              |           Value("wood") |
| scala-cli . -- data.ios crate.content.item.content  | Value("strawberry jam") |
| scala-cli . -- data.ios bag.content.about           |          Value("scala") |
| scala-cli . -- data.ios bag.content.item.content    |                    None |
| scala-cli . -- data.ios backpack.content.count      |                    None |
| scala-cli . -- data.ios bin                         |                  Object |

