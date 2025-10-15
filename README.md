# Image Canvas with Drag & Drop
This is an Android application built with modern, declarative UI using Jetpack Compose. It demonstrates how to create a dynamic, interactive canvas where users can drag images from a carousel and drop them onto a central canvas. Once on the canvas, images can be moved, scaled, and rotated.The project is built with a focus on a clean, scalable architecture following Google's recommended practices.

# Features
- Drag and Drop: Long-press an image in the bottom carousel and drag it onto the canvas.
- Image Manipulation:•Pan: Drag an image on the canvas to move it around.
- Zoom: Use pinch gestures to scale images up or down.
- Rotate: Use a two-finger twist gesture to rotate images.
- Modern Tech Stack: Built entirely with Kotlin and the latest Android development tools.
- Clean Architecture: Follows a modern MVI (Model-View-Intent) architecture with a clear separation of concerns.

# Tech Stack & Architecture
This project leverages a modern Android tech stack:
- UI: Jetpack Compose for building the entire UI declaratively.
    -  ConstraintLayout for Compose: Used for managing complex and responsive screen layouts.
    -  LazyRow: For efficiently displaying the scrollable image carousel.

- Architecture:
    -  MVI (Model-View-Intent): User actions are modeled as "Intents" which the ViewModel processes to update its state.
    -  Clean Architecture: The project is structured into UI, Domain, and Data layers.
    -  UI Layer: Composable screens (CanvasScreen) and the CanvasViewModel.
    -  Domain Layer: Contains business logic, models (Image), and use cases (ImagesUseCase).
    -  Data Layer: ImagesRepository responsible for fetching data (currently mocked).
    - Dependency Injection: Hilt is used to manage dependencies throughout the app, simplifying the architecture and improving testability.
    - Asynchronous Programming: Kotlin Coroutines and Flow are used for managing background tasks and handling asynchronous data streams from the repository to the UI.