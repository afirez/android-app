# Dagger2 入门系列 1 之 @Inject 与 @Component

二话不说，我反手就是一个。。。
> Dagger2 是一个依赖注入框架。

## 1. 添加依赖

```
dependencies {
    implementation 'com.google.dagger:dagger:2.15'
    annotationProcessor 'com.google.dagger:dagger-compiler:2.15'
}
```

## 2. 添加注解 @Inject

分别在 InjectComponentActivity 的 User 成员变量和 User 的构造函数上添加 @Inject 依赖

```
public class InjectComponentActivity extends AppCompatActivity {

    @Inject
    User user;
}

public class User {

    private String name;

    @Inject
    public User() {
        this.name = "afirez";
    }
}
```

> 在这里， 我们可以看到 @Inject 的 2 种情境：
>   1. 成员变量 user 上的 @Inject，告诉 Dagger 我需要一个 User 对象。
>   2. 构造函数 User 上的 @Inject，告诉 Dagger 我可以提供 User 对象。
>
> 此外，构造函数上的 @Inject，还会告诉 Dagger 自己初始化时，如果有需要注入依赖的成员变量，也帮自己一并注入。
> @Inject 还有一种情境，那就是方法上的 @Inject。我们现在先了解上述 2 种情境就够了。


## 3. 创建 Component 类

```
@Component
public interface InjectComponentComponent {

    void inject(InjectComponentActivity activity);

}
```

> 在这里，我们可以看到 ：
>  1. @Component 注解的 InjectComponentComponent，将告诉 Dagger，InjectComponentComponent 是依赖注入器。
>  2. 其中，提供 void inject(InjectComponentActivity activity) 方法，将告诉 Dagger，InjectComponentComponent 可以向 InjectComponentActivity 注入依赖。


## 4. 在 AS 中选择菜单 build -> Make Project

![AS 编译后将生成 3 个 源文件] (https://raw.githubusercontent.com/afirez/android-app/master/arts/dagger-a-make.png)

AS 编译后将生成 3 个 源文件:
  1. DaggerInjectComponentComponent
  2. InjectComponentActivity_MembersInjector
  3. User_Factory

其中，DaggerInjectComponentComponent 为 InjectComponentComponent 的子类。

## 5. 注入

最后我们可使用生成的 DaggerInjectComponentComponent 来完成注入， 成员变量 user 将被赋值。

```
public class InjectComponentActivity extends AppCompatActivity {

    @Inject
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inject_component);

        // 注入
        DaggerInjectComponentComponent.builder()
                .build()
                .inject(this);

        Log.i("Dagger2", "user.name = " + user.name);
        Toast.makeText(this, "user.name = " + user.name, Toast.LENGTH_SHORT).show();
    }
}
```

## 6. 运行

![运行效果图] (https://raw.githubusercontent.com/afirez/android-app/master/arts/dagger-a-run.png)

感觉和 ButterKnife.bind(this) 一样啊， 这么帅的吗？So easy！不行，我要试试了。。。

## 7. 为什么用 Dagger2

> 简单点，Coding 的方式简单点。。。就不能简单点吗？？？

> 我就初始化一个 user 成员变量， 写个 user = new User（）已经很累了好吗，你给我增加这么多类，就不怕我拿鸡蛋擂你么？？？

咱先稳一波，拿鸡蛋想一想，Dagger2 给咱带来了什么？

> 1. InjectComponentActivity （依赖需求方） 不用自己去关注 user 成员变量的初始化。
> 2. 如此这般，我们在改变 User 的实现时，换个 User 的子类，在依赖提供方更换依赖就完事了。
> 3. 而 InjectComponentActivity （依赖需求方）对依赖注入耦合性也就降低了。
> 4. Demo 中的示例比较简单，依赖关系比较简单，增加那么多类，好像反而得不偿失。但就耦合性来讲，是降低了。
> 5. 当我们写的代码越来越复杂，依赖关系也将越来越复杂，不用 Dagger 不用依赖注入框架，就不那么容易维护了。相信老司机的你肯定感触很深。。。
> 6. 之所以选 Dagger2 作为依赖注入框架，是咱大 Google 接手 Dagger 后，对 dagger 进行了优化，减少了反射代码，用代码生成的方式代替，性能更佳。

好了，这个解释 so easy 吧，如果还有问题，老哥，吐血 ing.jpg ...