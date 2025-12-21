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
    "items": [ /* List of `item_id`'s */ ],
    "components": { /* Components filter */ },
    "mode": /* Component filter mode: any | all | only */,
    "model": /* Model ID */
}
```
> Where:
> - `items` — A list of item IDs used to filter items.
>   - To apply the model to all items, use "*all*".
> - `components` — A J`SON object used to filter items by components.
>   - To apply the model regardless of components, omit this field.
> - `mode` — Controls how the components filter is applied.
>   - `any` — The item must contain at least one of the listed components.
>   - `all` — The item must contain all listed components. Other components are ignored.
>   - `only` — The item must contain only the listed components and no others.
> - `model` — The model location.
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
"none": { // You can overwrite it
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

For other contexts, it is not necessary to rewrite the profile exactly, it is enough to specify only the necessary parameters in
the profile. The rest will be taken from the `none` context.

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

---

<details>
<summary>Как это работает</summary>

<details>
<summary>#1 Регистрация моделей</summary>

Создайте файл `cim_models.json` в вашем ресурспаке по пути `assets/<mod_id>/cim_models.json` (**Не используйте `cim` в
качестве `mod_id`**)

Затем впишите следующее:
```json
{
    "entries": [
        // Список элементов
    ]
}
```
Добавьте новый элемент в список entries со следующей структурой:
```json
{
    "items": [ /* Список `item_id` */ ],
    "components": { /* Фильтр компонентов */ },
    "mode": /* Режим фильтрации компонентов: any | all | only */,
    "model": /* `model_id` */
}
```
> Где:
> - `items` — Список предметов (`item_id`), используемых для фильтрации.
>   - Укажите "*all*" для замены модели всех предметов.
> - `components` — JSON-объект, используемый для фильтрации предметов по компонентам.
>   - Не указывайте этот параметр, если требуется заменить модель вне зависимости от наличия компонентов.
>   - `mode` — Режим фильтрации компонентов.
>   - `any` — Предмет должен содержать любой из указанных компонентов.
>   - `all` — Предмет должен содержать все указанные компоненты. Остальные компоненты игнорируются.
>   - `only` — Предмет должен содержать только указанные компоненты. При отсутствии хотя бы одного или наличии лишних —
предмет игнорируется.
> - `model` — Идентификатор модели.
>   - Формат: <namespace/mod_id>:<folder_name>, где `folder_name` — имя директории внутри cim_models/.

</details>

<details>
<summary>#2 Model properties setup</summary>

Если требуется заменить только модель и/или анимации, в директории `folder_name` должны присутствовать следующие файлы:
```
/cim_models/<model_id>/
|- model.geo.json   | Файл модели предмета
|- texture.png      | Текстура модели
|- animations.json  | Анимации модели (необязательно)
\- display.json     | Трансформации отображения (необязательно)
```
Необходимо использовать **именно эти имена файлов**.

В противном случае, если требуется более детальная настройка модели, необходимо создать файл `properties.json` по пути
`/cim_models/<model_id>/properties.json` со следующим содержимым:
```json
{
    "name": /* Название модели (в данный момент не используется) */,
    "authors": /* Список авторов (в данный момент не используется) */,

    "disable_ground_bobbing": true,   // Отключает «парение» предмета (движение вверх-вниз)
    "disable_ground_spinning": true,  // Отключает вращение предмета на земле

    "display_context": {
        /* Связь между item_display_context и cim_profile */
    }
}

```
Поле `display_context` задаёт профиль по умолчанию для `item_display_context`, равного `none`:
```json
"none": { // Вы можете его перезаписать
    "model": "./model.geo.json",
    "texture": "./texture.png",
    "animations": "./animations.json",
    "display": "./display.json"
}
```
> Где:
> - `model` — Путь к ресурсу модели. Для локальных ресурсов используйте префикс `./`.
> - `texture` — Путь к текстуре модели. Для локальных ресурсов используйте префикс `./`.
> - `animations` — Путь к файлу анимаций. Необязательный параметр. Используйте `./` для локальных ресурсов.
> - `display` — Путь к файлу трансформаций отображения. Необязательный параметр. Используйте `./` для локальных ресурсов.

Для других контектов не обязательно переписывать профиль в точь-точь, достаточно указать только нужные параметры в
профиле. Остальные же будут взяты из `none` контекста.

</details>

</details>

</details>