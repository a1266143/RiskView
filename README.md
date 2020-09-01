# RiskView
风险评估View

![image](https://github.com/a1266143/RiskView/blob/master/screenshort.png)

How to
To get a Git project into your build:

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.a1266143:RiskView:TAG'
	}

please Replace TAG with [![](https://jitpack.io/v/a1266143/RiskView.svg)](https://jitpack.io/#a1266143/RiskView)