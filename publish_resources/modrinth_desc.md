# CIM | Custom Item Model
<details open>
<summary>English</summary>

## Adds support for fully-custom animated item models using simple vanilla-style 'JSON'

---

<details>
<summary>Requires mods</summary>

- [owo-lib](https://modrinth.com/mod/owo-lib)
- [GeckoLib](https://modrinth.com/mod/geckolib)

</details>

---

<details>
<summary>How it works</summary>

<details>
<summary>#1 Model registration</summary>

Create the file `cim_models.json` in your resource pack namespace at
`assets/<namespace>/cim_models.json` (**Do not use the `cim` namespace!**)

Then write the following content:
```json
{
    "entries": [
        // List of entries
    ]
}
```
Add a new element to entries with the following structure:
```json
{
    "items": [ /* List of item IDs */ ],
    "components": { /* Components filter */ },
    "mode": /* Component filter mode: any | all | only */,
    "model": /* Model ID */
}
```
> Where:
> - items — A list of item IDs used to filter items.
>   - To apply the model to all items, use "*all*".
> - components — A JSON object used to filter items by components.
>   - To apply the model regardless of components, omit this field.
> - mode — Controls how the components filter is applied.
>   - any — The item must contain at least one of the listed components.
>   - all — The item must contain all listed components. Other components are ignored.
>   - only — The item must contain only the listed components and no others.
> - model — The model location.
>   - Format: namespace:folder_name, where folder_name is a directory inside cim_models.

</details>

<details>
<summary>#2 Model properties setup</summary>

If you only need to replace the model and/or animations, the following files must be present in the `folder_name`
directory:
```
/cim_models/<model_id>/
|- model.geo.json  | Item model file
|- texture.png     | Texture for model
|- animations.json | Animations for model (optional)
\- display.json    | Display transform for model (optional)
```
You **must use these exact file names**.

Otherwise, if you need more detailed model configuration, you must create a `properties.json` file at
`/cim_models/<model_id>/properties.json` with the following content:
```json
{
    "name": /* Model name (currently unused) */,
    "authors": /* List of authors (currently unused) */,

    "disable_ground_bobbing": true,   // Disables item floating (up-down movement)
    "disable_ground_spinning": true,  // Disables item spinning on the ground

    "display_context": {
        /* Mapping between item_display_context and cim_profile */
    }
}
```
The display_context field defines the default `profile` for the `item_display_context` equal to none:
```json
{
    "model": "./model.geo.json",
    "texture": "./texture.png",
    "animations": "./animations.json",
    "display": "./display.json"
}
```
> Where:
> - model — Path to the model resource. Use the ./ prefix for local resources.
> - texture — Path to the model texture. Use the ./ prefix for local resources.
> - animations — Path to the animations file. Optional. Use the ./ prefix for local resources.
> - display — Path to the display transform file. Optional. Use the ./ prefix for local resources.

</details>

</details>

</details>

<br />

---

<br />

<details>
<summary>Русский</summary>

# Добавляет поддержку полноценных кастомных и анимируемых моделей предметов через обычный 'JSON'</h2>

---

<details>
<summary>Зависимости мода</summary>

- [owo-lib](https://modrinth.com/mod/owo-lib)
- [GeckoLib](https://modrinth.com/mod/geckolib)

</details>

</details>