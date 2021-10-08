# ComicApp
Well, my thoughtprocess..Right in the beginning I chose to focus on the 5 first features in the task-list.I realized quickly (and correctly, I hope) that I would not be dealing 
with an api that returns lists of any kind - and since there is 2513 comics at the moment I made the decision not to even try to fetch them one by one. Instead I did a somewhat 
ugly (but fully working) webscrape from Xkcd archive page. Where I finally got the entire list with dates, titles, id:s(numbers). This scrape seems to work well since the list 
in the app has been growing since I started (more comics have appeared). After that I put the list in a recyclerview and made a search-filter, where you can type in numbers or 
letters and the list updates accordingly. I also added the option to type "fav" to get all your favorites. Finally I included a small heart-symbol to show whether the 
comic is saved in favorites or not (for which I use room). At this point I ran into some trouble when trying to save the images in the Room-database - but solved it after some 
googling. I did not want to resort to Glide's internal caching. For some reason I was very meticulous with getting the app to work well even if you loose the internet connection 
and then regain it again (please try that out:) etc. I think I have all scenarios covered and I've been testing the app quite a lot manually. Anyway : when you click on an item 
in the recyclerview it takes you to the detail-view - there you see the comic itself plus the title ofcourse. I did not include anything else in the detailview since you can probably 
see that, that wouldn't really be an issue. There is - however - a button which takes you to a wepview-page that shows the explantion for the comic in question. I started scraping 
this also for a few hours, but then I decided to opt for the simple webview instead. Its probably the part of the app that´s the least pretty! But it was really a question of 
time running out. Also in the detail view you have an option to save to favorites and a button that changes it's function to "remove from favs" as soon as it has saved. Well, I 
think that is pretty much it. I used an MVVM-architechture, so all fetching from the internet and database happens in the repositoryImpl, most of the businesslogic is in viewmodels 
and then I have 3 activities that handle the explicit ui-stuff. This was pretty much the same structure that I have used before so it didn't pose any major issues. Project has now been updated with (amongst other things) noitifications for new comics. A jobScheduler runs every 2 hours to check if there are any new comics available. Once clicking on the "new" icon next to the comic, the notification is cancelled. Also there is a landscape design available for horizontally oriented comics. The app is now totally stable as far as I can tell.  
