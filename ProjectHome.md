# Azimuth Code Project #

Open source, free access software for scientific numerical simulations and data analysis.

The implementation is driven by the demands of the Azimuth project (see below), currently open **coding challenges** are listed [here](http://code.google.com/p/azimuthproject/wiki/Challenges).

Solved coding challenges are listed [here](http://code.google.com/p/azimuthproject/wiki/SolvedChallenges).

**Coding standards** and best practices are explained [here](CodingStandardsAndGuidelines.md).

# Introduction #

This is the open source project of the Azimuth Project: [Home Page](http://www.azimuthproject.org/azimuth/show/HomePage)

The Azimuth Code Project tries to provide clean, well documented implementations of basic numerical algorithms, using up-to-date tools and paradigms from software engineering. It is associated to the Azimuth Project, which is a plan to create a focal point for scientists and engineers interested in saving the planet, and make clearly presented, accurate information on the relevant issues easy to find.

# Participation #
Applicants need a google account and can notify us of their interest to join on the Azimuth Forum (see links).

# Motivation and Objectives #
Many sciences rely on computer code for simulations and analysis of data. Since results in science should be reproducible, the code used for published scientific results needs to be published as well. Therefore, one objective of this project is to

**show how computer code can easily be versioned and published along with any scientific results that are based on the code, by example.**

Scientific results belong to humanity, therefore

**all code used in science should be open source, everyone should be allowed to use it.**

A basic paradigm of science is that in principle, it should be possible for every fellow scientist to understand what another scientist has done. Therefore, any used code needs to be as easy to understand as possible. This project will therefore

**show how code can be documented and**

**show how code can be structured using established paradigms from software engineering, by example,**

in order to make it as easy as possible for others to understand it.

# Technical Choices #
The primary objective of this project is to serve as a repository for code used on the Azimuth project. Committers are therefore free to use any kind of tools they deem appropriate to solve their problems, and may document and commit it here. This also applies to scripts written for commercial or non-commercial packaged software.

This project is also about the question which paradigms of modern software engineering may be of use to scientific computing. The use of established paradigms from software engineering suggest that one should use

**object orientation and**

**automatic memory and thread handling as an additional abstraction layer.**

The first point rules out programming languages like C, the second one languages like C++. Both points suggest the use of languages like Java, a language of the .NET framework etc. On the other hand software used in scientific computing often relies on the ability to carefully manage memory and to take care especially to the efficient use of cache hierarchies, which suggests that languages with automatic memory management are useless.

This is one example where requirements in scientifc high performance computing and general software systems differ, which has lead to the current schism of the world of software creation into general purpose software engineering and scientific computing.
On this project we will try to find out which principles of software engineering are useful to scientific computing and where and why one encounters problems. For this reason, the use of "modern" programming languages and other principles from software engineering is encouraged.
