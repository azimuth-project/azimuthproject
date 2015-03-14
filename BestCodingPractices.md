Making code easy to understand.

# Introduction #

This page will list some general best coding practices. Hopefully, adhering to these practices will lead to code that is easier to read and understand.

**Note**: If your code is hard to understand then ultimately it will be thrown away and reimplemented by others. If you _don't want to work for the trash can_, you need to invest some effort into making your code understandable to other developers.

Naturally this page is and will always be work in progress.

Right now it is inspired by the point of view of a developer with background in general software development when confronted with code developed in scientific computing, like global climate models.

We will use C resp. C++ resp. Java syntax on this page.

# Details #

## Use Inline Comments ##
Of course every developer writes code that is self explaining :-)
but sometimes there are design decisions or caveats that are not easy to see. This is what inline comments are for.

Don't use comments to explain what the code is doing, every reader can see that for herself. So don't write
```
// i is an integer that is set to 1
int i = 1;
```
Instead explain **why** the code was written that way, what assumptions
you made and state pre- and postconditions.
An example of a useful inline comment explaining an assumption
would be this:
```
// i will be used to count the number of evaluation of all parameterizations at all grid points
// in every timestep, so care should be taken that there 
// does not occur an overflow if the scales are refined in later runs. 
int i = 1;
```
This comment explains that the author assumed that the range
of an integer would be enough for now, but that in the future
there may be situations where one should use a long instead.

Another example:
```
// now we know that all instances except "erg"
// are properly initialized, we can see to supplying erg
// with all necessary data to connect to the file system:
.
.
.
// now erg has all necessary data to connect to the file system.
```
These comments explain pre- and postconditions. If there is a bug report like "software does not write anything to the file system" because  the instance "erg" was not supplied
with the necessary password to connect to the file system,
a developer looking for the cause of the bug will instantly figure out, with the help of the comments, that the bug must be somewhere between
the first and the second comment of the example.

## Choose Meaningful Variable Names ##
A variable name should tell the reader what the variable represents. Don't use too many abbreviations.

Don't shy away from long names, editors today offer auto completion, and every developer should be able to type faster that she is able to think anyway.

So don't do something like this:
```
fricklepitz_35 = fricklepitz_2 * fricklepitz_67;
```
Is this statement correct? I don't know...

If we write instead
```
bookprice = inflationrate * ISBN;
```
we are able to conclude that, most probably, something is wrong, without knowing the rest of the code.

## Bundle Your Code Into Little Chunks: Methods ##
If some lines of code work together to achieve one definite goal, put them into their own method. This will make the reuse easier, but it will also make it easier for the reader to understand your code.
The following example creates a new file and the means to write to it:
```
String fileName = "newFile";

File file = new File(fileName);

file.createNewFile();

BufferedWriter out = new BufferedWriter(new FileWriter(file));
```
Now we have four lines of code with a high probability that this kind of functionality will be used more than once. If we put these for lines in a method with the signature
```
public static Writer createNewFile(String fileName)
```
there are multiple benefits: One is that the code is now reusable by anyone else, the second one is that the readers of our code don't have to parse and understand 4 lines of code, but only one line:

```
BufferedWriter out =  createNewFile("newFile");
```
In addition, the line tells the reader actively what it does: "createNewFile", by the means of the method name. The readers are free to choose if they trust the method and move on, of if they need to look at the method in more detail.

## Keep Connected Lines Close Together ##
Lines of code that depend on one another should be kept as close as possible. Remember that your readers won't remember the lines of code that they read two minutes ago, assume that they remember the last couple of lines of code only. So while in C we have to declare variables at the beginning of a code block
```
int index = 2;
.
. // lot's of things done here
.
for(; index < upperbound; index++) // where does "index come from"?
{
  // do something here
}
```
A further dirty trick is to initialize the variable to 2, of course. Thankfully most modern programming languages allow the definition of variables where they are needed first, so in Java we can rewrite this piece of code to
```
.
. // lot's of things done here
.
for(int index = 2; index < upperbound; index++) // now it's hard to misunderstand the meaning of 'index'
{
  // do something here
}
```

## Keep Nested Blocks to at most 4 ##
In the C programming language family, a code block is delimited by curly brackets. Code blocks can stand on their own, but their primary use is to enclose loops like "for" or "while" loops, or conditional statements. It is possible to have conditional statement inside a loop inside a loop like this:
```
for(int i = 0; i < firstBound; i++)
{
  for(int j = 0; j < secondBound; j++)
  {
     int r = 10;
     while(r > j)
     {
        // do something here
        r = i * j % 3
        if(r = 2)
        {
            break;
        }  // end of if  
     }  // end of while(r > j)
  } // end of for(j...)
} // end of for(i...)
```
This example has a "nesting level" of 4, which means we need 4 opening and 4 closing curly brackets.
It is a matter of experience that most readers have problems to understand a piece of code that has a nesting level of more than 4. This can be avoided by moving code blocks to their own methods, for example.
In our case we could refactor the code to
```
for(int i = 0; i < firstBound; i++)
{
  for(int j = 0; j < secondBound; j++)
  {
      doInnerLoopComputation(i, j);
  } // end of for(j...)
} // end of for(i...)
```
to reduce the nesting level from 2 to 4.