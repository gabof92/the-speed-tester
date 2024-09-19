*   nineoldandroid library (for Animations backwards compatibility) was added physically
    (.jar file) and referenced in the build.gradle (Module:app) file.

*   3 listviewanimation libraries (to animate ListViews) were added physically
    (.jar files) and referenced in the build.gradle (Module:app) file.

*   universal-image-loader library (for Image Utilities) was also added physically
    (.jar file) and referenced in the build.gradle file (Module:app),
    in order for the ImageLoader class in this lib to work, it has to be instantiated
    as the app opens (on create method of first activity).

*   'com.diogobernardino:williamchart:1.7.0' API (to create Charts)
    was added through the build.gradle (Module:app) file.

*   'pl.tajchert:waitingdots:0.2.0' API (to animate dots for waiting times)
    was added manually from the source code in the github repository web page below:
    https://github.com/tajchert/WaitingDots/tree/master/lib/src/main
    Since the library uses API 11 i had to copy the classes manually to replace
    the Animation imports and use the ones in the ninoldsandroid library.
    The code copied is in the "WaitingDots" package ("DotsTextView" and "JumpingSpan" classes)
    and in the res/values folder is the "attrs-waiting_dots.xml" file that contains
    the styleable for the "DotsTextView" class to work.

*   The WilliamChart API requires a Robotto-Regular.ttf file to be
    located at the root of the assets folder in order to work properly.

*   The MaterialRippleLayout class was Modified
    to be able to use it for api 10, nineoldandroid lib was added
    and imports were changed in that class to use that lib;
    also, the isInScrollingContainer() method was modified to check for API version.

 *  The KenBurnsView class was also modified for backwards compatibility,
    imports for the animations were changed to reference nineoldandroid lib classes.

 *  For Google Analytics the following code was added to the gradle files
        ~ build.gradle (Module: app):
            android{
                apply plugin: 'com.google.gms.google-services'
            }
            dependencies{
                compile 'com.google.android.gms:play-services-analytics:8.3.0'
            }
        ~ build.gradle (Project: TheSpeedTester):
            dependencies{
                classpath 'com.google.gms:google-services:1.5.0'
            }

 *  In order to try to fix a bug that occurs in some Samsung phones with Android 4.2
    the following code was added to the proguard-rules.pro file:
        -keep class !android.support.v7.internal.view.menu.**,** {*;}