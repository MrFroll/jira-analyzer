# jira-analyzer
This application extracts data from Jira to do some analysis.

An attempt to demonstrate Common-Closure Principle (CCP) was made.
You can [find in wikipedia that][1] "CCP states that the package should not have
more than one reason to change". To check deviations from this principles in real project component
graph was constructed. Component graph consists of vertexes - components and edges - jira issues.
Two components has common edge if there is jira issue that links this components.
The Graph is weighted. The weight of edge linking two components is a number of issues linking this
two components.
> TODO: weight vertexes too. So edge weight could be normalized as follow 2*w_ab /(w_a + w_b) where
> w_ab - weight of edge linking vertexes a and b. w_a or w_b are number of vertexes out of a or b.

As an example, Apache Camel project has been got.

Originally this was a Python project. You can find the resulting graph on [my twitter][graph].
But unfortunately sources have been lost and I decided to rewrite it with Java.

P.S.: As I'm reading Uncle Bob's books now, [Clean Architecture] and [Clean Code],
I'm trying use principles described there. My target to see advantages and disadvantages.

Feel free to contact me by mail@mrfroll.com
and visit my site www.mrfroll.com

[1]: https://en.wikipedia.org/wiki/Package_principles
[Clean Architecture]: https://www.amazon.com/Clean-Architecture-Craftsmans-Software-Structure/dp/0134494164
[Clean Code]: https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882
[graph]: https://twitter.com/rkostkin/status/824999630475784195