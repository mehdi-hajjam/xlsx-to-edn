# xlsx-to-edn

A Clojure library designed to transform Excel (.xlsx) hospital reports into .edn files in the context of the prais2cljs project.

## Usage

1. Set up the `resources` folder so that it contains your `.xlsx` file, along with the current `data.edn` file.

2. Rename the `data.edn` file to `previous-data.edn`.

3. Execute `lein run "your-file.xlsx" number-of-hospitals`

A new `data.edn` file is created, compiling the data from `previous-data.edn` with the report's data, in a single map. The new data is found under the key corresponding to the maximum key in `previous-data.edn` incremented by 1. For instance, if the latest data in `previous-data.edn` is under the key :2019, the newly imported data will be found under the key :2020.

### Precautions

Make sure:
- that the `.xlsx` file is in the same format as `prais2019.xlsx`;
- to update the hosp-data map in the code in case there are new hospitals in the project;
- to enter the right number of hospitals that are in the file.

Otherwise, there will be issues while loading the new data.
