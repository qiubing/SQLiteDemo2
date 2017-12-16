
# SQLite简介

## 一、简介

SQLite是一款轻量级的==关系型数据库==，它的运算速度非常快， 占用资源很少，通常只需要几百K的内存就足够了，因而特别适合在移动设备上使用。**SQLite不仅支持标准的SQL语法，还遵循了数据库的ACID事务**。

SQLite的数据库都是**以单个文件的形式**存在，这些数据都是以B-Tree的数据结构形式存储在磁盘上。

在事务处理方面，SQLite通过**数据库级上的独占性和共享锁**来实现独立事务处理。这意味着多个进程可以在同一时间从同一数据库读取数据，但只有一个可以写入数据。在某个进程或线程想数据库执行写操作之前，必须获得独占锁。在获得独占锁之后，其他的读或写操作将不会再发生。

SQLite采用**动态数据类型**，当某个值插入到数据库时，SQLite将会检查它的类型，如果该类型与关联的列不匹配，SQLite则会尝试将该值转换成该列的类型，如果不能转换，则该值将作为本身的类型存储，SQLite称这为“弱类型”。但有一个特例，如果是INTEGER PRIMARY KEY，则其类型不会被转换，会报一个“datatype missmatch”的错误。

概括来讲，SQLite支持NULL、INTEGER、REAL、TEXT和BLOB数据类型，分别代表空值、整型值、浮点值、字符串文本、二进制对象。


## 二、使用
### 1.创建数据库

Android为了让我们能够更加方便地管理数据库，专门提供了一个SQLiteOpenHelper帮 助类，借助这个类就可以非常简单地对数据库进行创建和升级。
SQLiteOpenHelper是一个抽象类，这意味着如果我们想要使用它的话，就需要创建一个自己的帮助类去继承它。SQLiteOpenHelper中有两个抽象方法，分别是onCreate()和onUpgrade()，这两个方法分别实现创建、升级数据库的逻辑。

SQLiteOpenHelper中还有两个非常重要的实例方法 ， getReadableDatabase()和getWritableDatabase()。这两个方法都可以创建或打开一个现有的数据库（如果数据库已存在则直接打开，否则创建一个新的数据库），并返回一个可对数据库进行读写操作的对象。构建出SQLiteOpenHelper 的实例之后，再调用它的getReadableDatabase()或getWritableDatabase()方法就能够创建数据库了，数据库文件会存放在/data/data/<package name>/databases/目录下。 

SQLite不像其他的数据库拥有众多繁杂的数据类型，它的数据类型很简单，integer表示整型，real表示浮点型，text表示文本类型，blob表示二进制类型。

### 2.查看数据库

数据库文件会存放在/data/data/<package name>/databases/目录下。通过adb shell进入到设备控制台，然后使用cd命令进入到数据库的目录下，用ls查看该目录里的文件，可以看到我们定义的数据库文件，数据库文件以".db"结尾。

借助SQLite命令来打开数据库，执行键入sqlite3 <数据库名称>即可，例如：

	sqlite3 bookstore.db

这时就已经打开了bookstore.db数据库，现在就可以对这个数据库中的表进行管理了。 首先来看一下目前数据库中有哪些表，键入.table 命令。

	sqlite> .table


这里还可以通过.schema 命令来查看它们的建表语句。

	sqlite > .schema


### 3.使用SQLiteOpenHelper对数据库进行版本管理

为了实现对数据库版本进行管理,Android系统为我们提供了一个名为SQLiteOpenHelper的抽象类，必须继承它才能使用。SQLiteOpenHelper类提供了两个重要的方法，分别是onCreate(SQLiteDatabase db)和onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)，**前者用于初次使用软件时生成数据库表，后者用于升级软件时更新数据库表结构**。

当调用SQLiteOpenHelper的getWritableDatabase()或者getReadableDatabase()方法获取用于操作数据库的SQLiteDatabase实例的时候，如果数据库不存在，android系统会自动生成一个数据库，接着调用onCreate()方法，onCreate()方法在初次生成数据库时才会被调用，在onCreate()方法里可以生成数据库表结构及添加一些应用使用到的初始化数据。

onUpgrade()方法在数据库的版本发生变化时会被调用，一般在软件升级时才需改变版本号，而数据库的版本是由程序员控制的，假设数据库现在的版本是1，由于业务的变更，修改了数据库表结构，这时候就需要升级软件，升级软件时希望更新用户手机里的数据库表结构，为了实现这一目的，可以把原来的数据库版本设置为2，并且在onUpgrade()方法里面实现表结构的更新。当软件的版本升级次数比较多，这时在onUpgrade()方法里面可以根据原版号和目标版本号进行判断，然后作出相应的表结构及数据更新。

getWritableDatabase()和getReadableDatabase()方法都可以获取一个用于操作数据库的SQLiteDatabase实例。但getWritableDatabase() 方法以读写方式打开数据库，一旦数据库的磁盘空间满了，数据库就只能读而不能写。getReadableDatabase()方法先以读写方式打开数据库，如果数据库的磁盘空间满了，就会打开失败，当打开失败后会继续尝试以只读方式打开数据库。

### 4.使用SQLiteDatabase操作SQLite数据库

Android提供了一个名为SQLiteDatabase的类，该类封装了一些操作数据库的API，使用该类可以完成对数据进行添加(Create)、查询(Retrieve)、更新(Update)和删除(Delete)操作（这些操作简称为CRUD）。对SQLiteDatabase的学习，我们应该重点掌握execSQL()和rawQuery()方法。 execSQL()方法可以执行insert、delete、update和CREATE TABLE之类有更改行为的SQL语句； rawQuery()方法用于执行select语句。

execSQL()方法的使用例子：

	SQLiteDatabase db = ....;  
  
	db.execSQL("insert into person(name, age) values('炸死特', 4)");  
  
	db.close();  


在拼接SQL语句时，要注意一些特殊符号，例如单引号，“&”符号，为了保证组拼好的SQL语句语法正确，必须对SQL语句中的这些特殊SQL符号都进行转义，显然，对每条SQL语句都做这样的处理工作是比较烦琐的。 SQLiteDatabase类提供了一个重载后的execSQL(String sql, Object[] bindArgs)方法，使用这个方法可以解决前面提到的问题，因为这个方法支持使用占位符参数(?)。

	SQLiteDatabase db = ....;  
  
	db.execSQL("insert into person(name, age) values(?,?)", new Object[]{"炸死特", 4});   
  
	db.close();  

execSQL(String sql, Object[] bindArgs)方法的第一个参数为SQL语句，第二个参数为SQL语句中占位符参数的值，参数值在数组中的顺序要和占位符的位置对应。

SQLiteDatabase的rawQuery() 用于执行select语句，使用例子如下：

	SQLiteDatabase db = ....;  
  
	Cursor cursor = db.rawQuery(“select * from person”, null);  
  
	while (cursor.moveToNext()) {  
  
  		int personid = cursor.getInt(0); //获取第一列的值,第一列的索引从0开始  
  
  		String name = cursor.getString(1);//获取第二列的值  
  
  		int age = cursor.getInt(2);//获取第三列的值  
  
	}  
  
	cursor.close();  
  
	db.close();  

rawQuery()方法的第一个参数为select语句；第二个参数为select语句中占位符参数的值，如果select语句没有使用占位符，该参数可以设置为null。带占位符参数的select语句使用例子如下：


	Cursor cursor = db.rawQuery("select * from person where name like ? and age=?", new String[]{"%炸死特%", "4"});  


Cursor是结果集游标，用于对结果集进行随机访问。使用moveToNext()方法可以将游标从**当前行移动到下一行**，如果已经移过了结果集的最后一行，返回结果为false，否则为true。另外Cursor还有常用的moveToPrevious()方法（用于将游标从当前行移动到上一行，如果已经移过了结果集的第一行，返回值为false，否则为true）、moveToFirst()方法（用于将游标移动到结果集的第一行，如果结果集为空，返回值为false，否则为true）和moveToLast()方法（用于将游标移动到结果集的最后一行，如果结果集为空，返回值为false，否则为true ） 。


除了前面介绍的execSQL()和rawQuery()方法，SQLiteDatabase还专门提供了对应于添加、删除、更新、查询的操作方法： insert()、delete()、update()和query()。这些方法实际上是给那些不太了解SQL语法的人使用的，对于熟悉SQL语法的程序员而言，直接使用execSQL()和rawQuery()方法执行SQL语句就能完成数据的添加、删除、更新、查询操作。

**insert()方法的使用**

	SQLiteDatabase db = databaseHelper.getWritableDatabase();  
  
	ContentValues values = new ContentValues();  
  
	values.put("name", "炸死特");  
  
	values.put("age", 4);  
  
	long rowid = db.insert(“person”, null, values);//返回新添记录的行号，与主键id无关


不管第三个参数是否包含数据，执行Insert()方法必然会添加一条记录，如果第三个参数为空，会添加一条除主键之外其他字段值为Null的记录。

**delete()方法的使用**


	SQLiteDatabase db = databaseHelper.getWritableDatabase();  
  
	db.delete("person", "personid<?", new String[]{"2"});  
  
	db.close(); 

上面代码用于从person表中删除personid小于2的记录。

**update()方法的使用**


	SQLiteDatabase db = databaseHelper.getWritableDatabase();  
  
	ContentValues values = new ContentValues();  
  
	values.put(“name”, “炸死特”);//key为字段名，value为值  
  
	db.update("person", values, "personid=?", new String[]{"1"});   
  
	db.close();  

上面代码用于把person表中personid等于1的记录的name字段的值改为“炸死特”。

**query()方法的使用**

	SQLiteDatabase db = databaseHelper.getWritableDatabase();  
  
	Cursor cursor = db.query("person", new String[]{"personid,name,age"}, "name like ?", new String[]{"%炸死特%"}, null, null, "personid desc", "1,2");  
  
	while (cursor.moveToNext()) {  
  
         int personid = cursor.getInt(0); //获取第一列的值,第一列的索引从0开始  
  
          String name = cursor.getString(1);//获取第二列的值  
  
          int age = cursor.getInt(2);//获取第三列的值  
  
	}  
  
	cursor.close();  
  
	db.close();  

实际上是把select语句拆分成了若干个组成部分，然后作为方法的输入参数。

query(table, columns, selection, selectionArgs, groupBy, having, orderBy,limit)方法各参数的含义：

**table**：表名。相当于select语句from关键字后面的部分。如果是多表联合查询，可以用逗号将两个表名分开。

**columns**：要查询出来的列名。相当于select语句select关键字后面的部分。

**selection**：查询条件子句，相当于select语句where关键字后面的部分，在条件子句允许使用占位符“?”

**selectionArgs**：对应于selection语句中占位符的值，值在数组中的位置与占位符在语句中的位置必须一致，否则就会有异常。

**groupBy**：相当于select语句group by关键字后面的部分

**having**：相当于select语句having关键字后面的部分

**orderBy**：相当于select语句order by关键字后面的部分，如：personid desc, age asc;

**limit**：指定偏移量和获取的记录数，相当于select语句limit关键字后面的部分。


### 5.SQLite数据类型

一般的数据采用的固定的静态数据类型，而SQLite采用的是动态数据类型，会根据存入值自动判断。SQLite具有以下五种常用的数据类型：

**NULL**: 这个值为空值

**VARCHAR(n)**：长度不固定且其最大长度为n的字串，n不能超过4000。

**CHAR(n)**：长度固定为n的字串，n不能超过254。

**INTEGER**: 值被标识为整数,依据值的大小可以依次被存储为1,2,3,4,5,6,7,8.

**REAL**: 所有值都是浮动的数值,被存储为8字节的IEEE浮动标记序号.

**TEXT**: 值为文本字符串,使用数据库编码存储(TUTF-8, UTF-16BE or UTF-16-LE).

**BLOB**: 值是BLOB数据块，以输入的数据格式进行存储。如何输入就如何存储,不改变格式。

**DATA**：包含了年份、月份、日期。

**TIME**： 包含了小时、分钟、秒。


## 三、源码分析

SQLiteOpenHelper是SQLiteDatabase的一个辅助类，用来帮助在应用初始化时创建数据库表以及应用升级时更新数据库表。下面将从源码的角度来分析SQLiteOpenHelper的getReadableDatabase()和gegetWritableDatabase()方法。

3.1 SQLiteOpenHelper()

	public SQLiteOpenHelper(Context context, String name, CursorFactory factory, int version)
	{
        this(context, name, factory, version, null);
    }

	public SQLiteOpenHelper(Context context, String name, CursorFactory factory, int version,
            DatabaseErrorHandler errorHandler) {
		//版本小于1，则会抛出异常，因为初始创建的数据库版本为0，只有版本大于0时，才会触发调用onCreate()方法
        if (version < 1) throw new IllegalArgumentException("Version must be >= 1, was " + version);

        mContext = context;
        mName = name;
        mFactory = factory;
        mNewVersion = version;
        mErrorHandler = errorHandler;
    }

SQLiteOpenHelper的构造函数主要是保存参数传递的变量，为后面创建或打开数据库做准备。


3.2 SQLiteOpenHelper.getReadableDatabase()

	public SQLiteDatabase getReadableDatabase() {
        synchronized (this) {
            return getDatabaseLocked(false);
        }
    }

	private SQLiteDatabase getDatabaseLocked(boolean writable) {
		//如果数据库不为空，并且已经打开了，则直接返回
        if (mDatabase != null) {
            if (!mDatabase.isOpen()) {
                // Darn!  The user closed the database by calling mDatabase.close().
                mDatabase = null;
            } else if (!writable || !mDatabase.isReadOnly()) {
                // The database is already open for business.
                return mDatabase;
            }
        }

		// 如果正在初始化，则抛出异常
        if (mIsInitializing) {
            throw new IllegalStateException("getDatabase called recursively");
        }

        SQLiteDatabase db = mDatabase;
        try {
            mIsInitializing = true;
	
            if (db != null) {
				//writable为false
                if (writable && db.isReadOnly()) {
                    db.reopenReadWrite();
                }
            } else if (mName == null) {//数据库的名字，则创建一个空的数据库
                db = SQLiteDatabase.create(null);
            } else {
                try {
                    if (DEBUG_STRICT_READONLY && !writable) {//调试时信息
                        final String path = mContext.getDatabasePath(mName).getPath();
                        db = SQLiteDatabase.openDatabase(path, mFactory,
                                SQLiteDatabase.OPEN_READONLY, mErrorHandler);
                    } else {
                        db = mContext.openOrCreateDatabase(mName, mEnableWriteAheadLogging ?
                                Context.MODE_ENABLE_WRITE_AHEAD_LOGGING : 0,
                                mFactory, mErrorHandler);//打开或者创建数据库
                    }
                } catch (SQLiteException ex) {
                    if (writable) {
                        throw ex;
                    }
                    Log.e(TAG, "Couldn't open " + mName
                            + " for writing (will try read-only):", ex);
                    final String path = mContext.getDatabasePath(mName).getPath();
                    db = SQLiteDatabase.openDatabase(path, mFactory,
                            SQLiteDatabase.OPEN_READONLY, mErrorHandler);//如果以PRIVATE模式打开数据时抛出了异常，则以只读的方式打开数据库文件
                }
            }

            onConfigure(db);//配置数据库的一些信息，默认实现为空，在onCreate()、onUpdate()方法之前调用

            final int version = db.getVersion();//获取数据库的版本号，刚创建出来的数据库的版本号为0
            if (version != mNewVersion) {//版本号发生了变化
                if (db.isReadOnly()) {//如果是数据库是只读的，则抛出异常，因为只读数据库不能被升级
                    throw new SQLiteException("Can't upgrade read-only database from version " +
                            db.getVersion() + " to " + mNewVersion + ": " + mName);
                }

                db.beginTransaction();//以事务的方式执行
                try {
                    if (version == 0) {//初始版本为0，说明是第一次打开或者创建数据库
                        onCreate(db);//回调onCreate()方法
                    } else {
                        if (version > mNewVersion) {//如果新的版本号比旧的版本号小，则把数据库进行降级处理
                            onDowngrade(db, version, mNewVersion);//降级处理
                        } else {//如果新的版本号比旧的版本号大，则把数据库进行升级处理
                            onUpgrade(db, version, mNewVersion);//升级处理
                        }
                    }
                    db.setVersion(mNewVersion);//设置新的版本号
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            onOpen(db);//在数据库打开之后调用，默认实现为空

            if (db.isReadOnly()) {
                Log.w(TAG, "Opened " + mName + " in read-only mode");
            }

            mDatabase = db;//保存打开的数据库文件
            return db;
        } finally {
            mIsInitializing = false;
            if (db != null && db != mDatabase) {
                db.close();
            }
        }
    }

可以看到，通过getReadableDatabase()方式打开数据库时，传递的writeable参数为false，最终调用的是getDatabaseLocked()方法进行打开数据库操作。在getDatabaseLocked()方法中的主要的操作有：

1. getDatabaseLocked()方法的调用是在获取独占锁synchronized后调用的，因此可以保证每次只有一个线程去操作数据库文件。
2. 如果数据库文件已经存在了，并且已经打开了，则直接返回该数据库；
3. 根据初始化SQLiteOpenHelper时设置的数据库名字，以PRIVATE模式去打开或者创建一个数据库文件。如果打开数据库文件失败，则尝试以只读方式打开数据库文件；
4. 数据库连接完成之后，可以调用onConfigure()方法对数据库进行配置操作，默认该方法是空实现；
5. 根据数据库版本号，决定是调用onCreate()方法还是调用onDowngrade()或者是onUpgrade()方法：
	1. 如果初始化数据库版本号为0，则表示是第一次打开或者创建数据库，则调用onCreate()方法；
	2. 如果新的数据库版本号大于旧的数据库版本号，则调用onUpgrade()方法，对数据库进行升级操作；
	3. 如果新的数据库版本号小于旧的数据库版本号，则调用onDowngrade()方法，对数据库进行降级操作；

6. 数据库打开之后，可以调用onOpen()方法进行一些配置操作，该方法模式是空实现；


3.3 SQLiteOpenHelper.getWritableDatabase()

	public SQLiteDatabase getWritableDatabase() {
        synchronized (this) {
            return getDatabaseLocked(true);//见3.2
        }
    }

可以看到，getWritableDatabase()和通过getReadableDatabase()方法最终都是调用getDatabaseLocked()方法，只是传递的wirteable参数不同。getReadableDatabase()方法传递的writeable参数为false，而getWritableDatabase()传递的参数为true。

## 四、参考文章

[android之存储篇_SQLite数据库](http://blog.csdn.net/jason0539/article/details/10248457)

[Androird SQLite应用详解](http://blog.csdn.net/liuhe688/article/details/6715983)

