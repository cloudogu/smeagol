# Demo-Inhalte

This page describes content structures and text elements so that user manual page screenshots can be produced in the same way.

## SCM-Manager

Create a Repository:
- Namespace: `documentation`
- Name: `my_repo`
- initialize the repository with a Readme

Create content:
- an empty `.smeagol.yml`
- add 2 further branches
  - `develop`
  - `feature/add_feature`

## Smeagol content

### Create a Home page

Under documentation -> my_repo fill a `Home` page in the course over 2-3 commits: 

```
# My Documentation

Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate

## Chapter title

Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

- Lorem ipsum dolor sit amet
- consectetur adipiscing elit

## Another chapter title

Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
```

### Create a second page

Under documentation -> my_repo create a **new** page with `PlantUML` content (please remove the following backslashes from the inner code fences to render valid PlantUML):

```
# Hello World!

Welcome to a Smeagol demo page. Here, have some PlantUML:

\```uml
class Car

Driver - Car : drives >
Car *- Wheel : have 4 >
Car -- Person : < owns
\```

# Even more documentation

More documentation goes here.
```
