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

If we need replace only model &(or) animations, to need contains next files in 'folder_name':
```
/cim_models/<model_id>/
|- model.geo.json  | Item model file
|- texture.png     | Texture for model
|- animations.json | Animations for model. Is not requier
\- display.json    | Display transform for model. Is not requier
```
Need to use that's file name!

Else, wee need to more detail setting model, to need create a 'properties.json' file
in '/cim_model/<model_id>/properties.json' with next content:
```json
{
    "name": /* A model name. Is don't use now */,
    "authors": /* A list authors. Is dont' use now */,
    
    "disable_ground_bobbing": true, // If we need to disable flying item (up-down slide)
    "disable_ground_spinning": true, // If we need to disable spinning item
    
    "display_context": { /* Pair element at 'item_display_context' and 'cim_profile' */ }
}
```
The `display_context` contains default profile for 'item_display_context' equal 'none':
```json
{
    // Local resources //
    
    "model": "./model.geo.json",
    "texture": "./texture.png",
    "animations": "./animations.json",
    "display": "./display.json"
}
```

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