# xlsx-to-edn

A small Clojure application designed to transform Excel (.xlsx) hospital reports into .edn files in the context of the prais2cljs project.

## Usage (general)

1. Go to the root repository and check that both the resources/ and target/ folder exist.

2. Check that the resources/ repository contains your latest data (those from the last reporting period) in a data.edn file. If this file doesn't exist but your data live in previous-data.edn, that is also fine.

3. Execute in a terminal `./convert.sh "your-file.xlsx" number-of-hospitals` where your-file.xlsx is the complete path to your excel file (e.g. `/home/user/project/prais2/prais2020.xlsx`), and number-of-hospitals is the exact number of hospitals reporting data for the period.
> You may need to turn the script into an executable first by running `chmod +x convert.sh`

## Usage (developers)

If you are a developer and have `Leiningen` installed, you can go through the following steps to test the code:

1. Set up the `resources` folder so that it contains your `.xlsx` file, along with the current `data.edn` file.

2. Rename the `data.edn` file to `previous-data.edn`.

3. Execute `lein run "your-file.xlsx" number-of-hospitals`

Should you change the code, you'll need to run `lein uberjar` to create a new jar for the application. You'd also possibly need to update the `convert.sh` script as well.

## Results

A new `data.edn` file is created, compiling the data from `previous-data.edn` with the report's data, in a single map. The new data is found under the key corresponding to the maximum key in `previous-data.edn` incremented by 1. For instance, if the latest data in `previous-data.edn` is under the key :2019, the newly imported data will be found under the key :2020.

### Precautions

Make sure:
- that the `.xlsx` file is in the same format as `resources/prais2019.xlsx`;
- to update the hosp-data map in the code in case there are new hospitals in the project;
- to enter the right number of hospitals that are reporting data in your file.

Otherwise, there will be issues while loading the new data.
