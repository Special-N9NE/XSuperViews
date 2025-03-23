<p align="center">
  <img src="https://github.com/Special-N9NE/XSuperViews/blob/master/XSuperViews.png?raw=true" alt="XSuperViews Logo" />
</p>

# XSuperViews 🚀

A powerful Android **custom views library** designed to simplify common UI patterns like **loading states**, **empty states**, **pagination**, **swipe-to-refresh**, and more — all with minimal setup and full customization.

[![](https://jitpack.io/v/Special-N9NE/XSuperViews.svg)](https://jitpack.io/#Special-N9NE/XSuperViews)

---

## ✨ Features
✅ Plug-and-play custom views  
✅ **Loading** and **Empty** states handling  
✅ Built-in **Swipe-to-Refresh** support  
✅ **Pagination / Load More** support  
✅ Customizable attributes  
✅ Designed for **easy extension** (more views coming soon...)  

---

## 📦 Available Views (Growing List)
- **XRecyclerView** — Enhanced RecyclerView with:
  - Loading / Empty view support
  - Swipe refresh
  - Auto load more
  - Optional dividers

> 🛠 More views will be added soon (e.g., XScrollView, XButton...)

---

## ⚙️ Gradle Setup
### 1. Add JitPack to your root `build.gradle`
```gradle
allprojects {
    repositories {
        maven { setUrl("https://jitpack.io") }
    }
}
```
### 2. Add the dependency
```gradle
dependencies {
    implementation("com.github.Special-N9NE:XSuperViews:v1.0")
}
```
👉 Check for the latest version on [JitPack](https://jitpack.io/#Special-N9NE/XSuperViews)

---

## 🚀 Quick Example (XRecyclerView)
### XML
```xml
<org.nine.xsuperviews.XRecyclerView
    android:id="@+id/xRecyclerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:emptyText="No data available"
    app:hasDivider="true"
    app:swipeRefreshEnabled="true"
    app:swipeRefreshColor="@color/colorPrimary"/>
```

### Kotlin
```kotlin
xRecyclerView.setLayoutManager(LinearLayoutManager(this))
xRecyclerView.setAdapter(yourAdapter)

// Show loading state
xRecyclerView.setLoading(true)

// Optional: Set custom empty view
xRecyclerView.setCustomEmptyView(customEmptyView)

// Optional: Set custom loading view
xRecyclerView.setLoadingView(customLoadingView)

// Load more listener
xRecyclerView.setNextLoadListener {
    // Fetch next page data here
}
```

---

## 🎨 XML Attributes (XRecyclerView)
| Attribute                 | Description                             | Default            |
|---------------------------|-----------------------------------------|--------------------|
| `app:emptyText`           | Text displayed in empty state            | `"No data provided"` |
| `app:hasDivider`          | Show item dividers                      | `false`            |
| `app:swipeRefreshEnabled` | Enables pull-to-refresh. If disabled, a ProgressBar or the customLoadingView will be displayed.| `false`            |
| `app:swipeRefreshColor`   | Swipe refresh indicator color           | -                  |

---

## 🔮 Roadmap
✅ `XRecyclerView` — DONE  
🚧 `XButton` — Coming Soon  
🚧 `XScrollView` — Coming Soon  
🚀 More customizable views in progress...

---

## 🤝 Contributions
Feel free to fork, improve, or suggest new views!  
👉 [Open an Issue](https://github.com/Special-N9NE/XSuperViews/issues)  
👉 [Create a Pull Request](https://github.com/Special-N9NE/XSuperViews/pulls)

---

## ✍️ Author
Made with ❤️ by **[Special-N9NE](https://github.com/Special-N9NE)**  
⭐ If you like this library, don’t forget to star the repo!
