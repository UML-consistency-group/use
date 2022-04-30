# USE工具代码分析

环境：idea，JDK8

## 运行配置

### 直接运行

直接运行main函数，保证环境正常。

打开Main.java，直接运行：

![image-20220428153155393](https://ln-markdown-image-bucket.oss-cn-beijing.aliyuncs.com/img/image-20220428153155393.png)

正常打开效果：

![image-20220428153238835](https://ln-markdown-image-bucket.oss-cn-beijing.aliyuncs.com/img/image-20220428153238835.png)

### 指定输入文件

配置idea运行环境指定输入文件：

![image-20220428153450972](https://ln-markdown-image-bucket.oss-cn-beijing.aliyuncs.com/img/image-20220428153450972.png)

设置配置

![image-20220428153707610](https://ln-markdown-image-bucket.oss-cn-beijing.aliyuncs.com/img/image-20220428153707610.png)

再次运行：

![image-20220428153737409](https://ln-markdown-image-bucket.oss-cn-beijing.aliyuncs.com/img/image-20220428153737409.png)

读入文件正常

### 使用论文样例

将输入文件重定向到demo/Demo.use中

![image-20220429165338269](https://ln-markdown-image-bucket.oss-cn-beijing.aliyuncs.com/img/image-20220429165338269.png)

运行，并读入instance.cmd

![image-20220429165415243](https://ln-markdown-image-bucket.oss-cn-beijing.aliyuncs.com/img/image-20220429165415243.png)

图形化界面显示如图

![image-20220429165551966](https://ln-markdown-image-bucket.oss-cn-beijing.aliyuncs.com/img/image-20220429165551966.png)

## 代码分析

### 程序入口Main

重要组件：

```java
Session session = new Session();
MModel model = null;
MSystem system = null;
```

session <-- system <-- model

#### 文件读入

```java
if (Options.specFilename != null) {
    try (FileInputStream specStream = new FileInputStream(Options.specFilename)){
        Log.verbose("compiling specification...");
        model = USECompiler.compileSpecification(specStream,
                                                 Options.specFilename, new PrintWriter(System.err),
                                                 new ModelFactory());
    }
	......
    ......
    // create system
    system = new MSystem(model);
}
```

`USECompiler.compileSpecification`方法进行了文件的编译，返回了UML模型的所有定义，包括OCL约束。

进入compileSpecification方法：

```java
/**
     * Compiles a specification.
     *
     * @param  in the source to be compiled
     * @param  inName name of the source stream
     * @param  err output stream for error messages
     * @return MModel null if there were any errors
     */
    public static MModel compileSpecification(InputStream in, 
                                              String inName,
                                              PrintWriter err,
                                              ModelFactory factory) {
        MModel model = null;
        ParseErrorHandler errHandler = new ParseErrorHandler(inName, err);
        
        ......
		
        USELexer lexer = new USELexer(aInput);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        USEParser parser = new USEParser(tokenStream);
        
        lexer.init(errHandler);
        parser.init(errHandler);
        
        ......
        
        return model;
    }
```

parser <-- tokenStream <-- lexer的流程进行了编译

#### GUI界面

GUI界面引用了session对象从而与model进行交互

```java
if (Options.doGUI) {
    Class<?> mainWindowClass = null;

    //创建mainWindowClass
    ......
	//创建部分结束
    
    try {
        if (pluginRuntime == null) {
            Log.debug("Starting gui without plugin runtime!");
            Method create = mainWindowClass.getMethod("create",
                                                      new Class[] { Session.class });
            Log.debug("Invoking method create with ["
                      + session.toString() + "]");
            // 调用了mainWindowClass的create方法，传入的参数为session
            create.invoke(null, new Object[] { session });
        } else {
            Log.debug("Starting gui with plugin runtime.");
            Method create = mainWindowClass.getMethod("create",
                                                      new Class[] { Session.class, IRuntime.class });
            Log.debug("Invoking method create with ["
                      + session.toString() + "] ["
                      + pluginRuntime.toString() + "]");
            // 调用了mainWindowClass的create方法，传入的参数为session
            create
                .invoke(null,
                        new Object[] { session, pluginRuntime });
        }
    } 
}
```

其中最重要的是`create.invoke(null, new Object[] { session })`，这里调用了mainWindowClass的create方法，传入的参数为session

#### Shell

```java
// create thread for shell
Shell.createInstance(session, pluginRuntime);
Shell sh = Shell.getInstance();
Thread t = new Thread(sh);
t.start();
```

创建shell时也遵循类似的方案，将session对象作为参数传递。

### MModel分析

use通过编译后返回一个MModel的对象

该对象有如下关键成员变量

```java
//模型的UML类定义
private Map<String, MClass> fClasses;
//模型UML类之间的关系的定义
private Map<String, MAssociation> fAssociations;
//模型的OCL约束定义
private Map<String, MClassInvariant> fClassInvariants;
```

#### OCL约束结构分析

use中的所有约束被封装成为`MClassInvariant`对象

该对象有`Expression`类的成员变量，存储具体的表达式

![image-20220428161343594](https://ln-markdown-image-bucket.oss-cn-beijing.aliyuncs.com/img/image-20220428161343594.png)

点击显示所有的实现类

![image-20220428161510980](https://ln-markdown-image-bucket.oss-cn-beijing.aliyuncs.com/img/image-20220428161510980.png)

![image-20220428161608473](https://ln-markdown-image-bucket.oss-cn-beijing.aliyuncs.com/img/image-20220428161608473.png)

（展示部分）

### Shell运行时过程分析

在main函数中调用t.start()方法后，会进入shell的`run()`函数中。

该函数循环接受指令：

```java
while (!fFinished) {
    ......

    String line = "";

    ......
    if (line != null) {
        if (!fReadline.doEcho()) {
            USEWriter.getInstance().protocol(line);
        }

        processLineSafely(line);
    } else {
        fFinished = !fReadlineStack.hasReadline();
        if (fFinished && Options.quiet) {
            processLineSafely("check");
        }
    }
}
```

其中，函数` processLineSafely(line)`会对命令进行处理，该函数调用了`processLine(String line)`函数，

完成所有命令后会调用`processLineSafely("check")`进行一次一致性检验，这次检查检查了model中的所有ocl约束。

#### processLine函数

这里面处理了各种命令

```java
if (line.startsWith("help") || line.endsWith("--help")) {
    cmdHelp(line);
} 
......
} else if (line.startsWith("!")) {
    cmdExec(line.substring(1).trim(), false);
} else if (line.equals("check") || line.startsWith("check ")) {
    cmdCheck(line);
} 
......
```

`cmdExec(line.substring(1).trim(), false)`会处理`!create`等命令，

`cmdCheck(line)`进行了检查。

##### cmdExec函数

```java
private void cmdExec(String line, boolean verbose) throws NoSystemException {

......
    
    MSystem system = system();
    MStatement statement = ShellCommandCompiler.compileShellCommand(
        system.model(),
        system.state(),
        system.getVariableEnvironment(),
        line,
        "<input>",
        new PrintWriter(System.err),
        verbose);

......
        
        
    try {
        if ((statement instanceof MEnterOperationStatement)
            || (statement instanceof MExitOperationStatement)) {

            system.execute(statement, false, true, true);
        } else {
            system.execute(statement);
        }
    } 
    
...... 
          
          
    } finally {
        fSession.evaluatedStatement(statement);
    }
}
```

其中`system.execute(statement)`执行了类似`!create`和`!set`等命令

该函数中：

```java
MStatement statement = ShellCommandCompiler.compileShellCommand(
    system.model(),
    system.state(),
    system.getVariableEnvironment(),
    line,
    "<input>",
    new PrintWriter(System.err),
    verbose);
```

调用`ShellCommandCompiler.compileShellCommand`方法，对命令进行了编译，并生成了相应的环境。

该方法调用了

```java
public static MStatement compileShellCommand(MModel model,
      MSystemState state, VariableEnvironment variableEnvironment,
      InputStream input, String inputName, PrintWriter errorOutput,
      boolean verbose)
```

该方法中：

```java
//构造AST
ASTStatement ast = constructAST(input, inputName, errorOutput, verbose);
        
//依据AST生成环境
MStatement statement = constructStatement(ast, inputName, errorOutput,
                                          state, model, variableEnvironment, verbose);
```

在后续过程中暂时未发现进行一致性检验的调用。

注意：

`MAttributeAssignmentStatement`中的`Expression`并不是一致性检验的表达式，而是赋值等的表达式，之前的分析认为它是一致性检验的表达式，这是错误的

### 结论

目前来看，对于shell调用中，`create`,`set`等方法执行时并不会进行一致性检验，只有在最后的时候会进行一次全局的一致性检验。