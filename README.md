# freitag

[![Clojars Project](https://img.shields.io/clojars/v/com.lambdaschmiede/freitag.svg)](https://clojars.org/com.lambdaschmiede/freitag)

A clojure library for public holidays.
Currently, only contains entries for Germany (2021, 2022)

## Usage

``` clojure
(require '[freitag.core :refer [query]])

;; All public holidays for January 2021 for the whole country
(query {:country :de
          :year 2021
          :month 1})
=>  ({:name "Neujahr", :month 1, :day 1})

;; All public holidays for January 2021 for the state "Baden-Wuerttemberg"
(query {:country :de
          :year 2021
          :month 1
          :state :bw})
=>  ({:name "Neujahr", :month 1, :day 1}
     {:name "Heilige drei Könige", :month 1, :day 6, :states #{:st :bw :by}})                    
```

## License

Copyright © 2021 lambdaschmiede GmbH

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
