**[EN] `CIM (Custom Item Model)` adds support for fully-custom animated item models using simple vanilla-style JSON.**

**[RU] `CIM (Custom Item Model)` добавляет поддержку полноценных кастомных и АНИМИРУЕМЫХ моделей предметов через обычный `JSON**

---

Requires mods / Требуются моды:
- [GeckoLib](https://modrinth.com/mod/geckolib)
- [owo-lib](https://modrinth.com/mod/owo-lib)

---

<details>
<summary>[RU]</summary>

# Как работает:

<details>
<summary>Регистрация модели</summary>

Необходимо создать файл `cim_models.json` в пути `<твой_ресурс_пак>/assets/<пространство_имён>/cim_models.json` с содержимым по следующему шаблону:
```json
{
    "entries": [
        {
            "items": [ "<item_id_here>" ],
            "components": { "<component_name>": <component_value> },
            "mode": "<any/all/only>",
            "model": "<model_id>"
        }
    ]
}
```
> Где:
> - `entries` - Список объектов с данными.
> - `items` - Список `item_id`, Можно указать как и без `пространство_имён` (пример `stick`), либо с ним (`mod_id:item_id`).
> - `components` - Список компонентов. Может быть как и просто **Строка и число**, так и другое, валидное для `JSON`:
>   - `"custom_model_data": 12345`.
>   - `"potion_contents": { "potion": "water" }`
> - `mode` - Режим фильтрации компонентов предметов:
>   - `any` - Предмет должен иметь хотя бы один из перечисленных компонентов (минимум 1).
>   - `all` - Предмет должен иметь все из перечисленных.
>   - `only` - Предмет должен иметь **только** все из перечисленных.
> - `model` - `ID` модели. Проще говоря, имя директории, в котором находится ваш `properties.json`:
>   - `<твой_ресурс_пак>/assets/<пространство_имён>/cim_models/<model_id>/properties.json` -> `<пространство_имён>:<model_id>`

</details>

<details>
<summary>Описание и поведение модели</summary>

Теперь нужно подготовить ресурсы для модели.
Если вам нужно заменить просто модель предмета, без контекста отображения (`display`), то можно не обязательно иметь `properties.json`. При необходимости - мод сгенерирует значения по умолчанию.
> - Будут сгенерированные такие данные как: имя модели, список авторов (1 автор) и профиль отображения:
>   - Контекст отображения будет содержать в себе бесконтекстный тип с путями к ресурсам по умолчанию, как:
>     - `display = none` - вне зависимости от типа отображения (в руке, у меню, на земле и т.д.) будет использован данный профиль:
>       - `model = ./model.geo.json`:
>         - Файл модели должен в таком случае находится в том же месте, где и лежит сам `properties.json` (благодаря префиксу `./` - мод понимает что это - локальный ресурс)
>         - Файл модели должен иметь конкретное имя `model.geo.json`.
>       - `texture = ./texture.png:
>         - Файл текстуры должен в таком случае находится в том же месте, где и лежит сам `properties.json` (благодаря префиксу `./` - мод понимает что это - локальный ресурс)
>         - Файл текстуры должен иметь конкретное имя `texture.png`.
>       - `animations = ./animations.json:
>         - Файл анимаций должен в таком случае находится в том же месте, где и лежит сам `properties.json` (благодаря префиксу `./` - мод понимает что это - локальный ресурс)
>         - Файл анимаций должен иметь конкретное имя `animations.json`.
>       - `display = ./display:
>         - Файл отображения должен в таком случае находится в том же месте, где и лежит сам `properties.json` (благодаря префиксу `./` - мод понимает что это - локальный ресурс)
>         - Файл отображения должен иметь конкретное имя `display`.

---

Если же нужно более подробно описать поведение модели - то заполните файл по следующему шаблону:
```json
{
    "name": "<model_name_here>",
    "authors": [ "<author_here>" ],
    
    "disable_ground_bobbing": <true/false>,
    "disable_ground_spinning": <true/false>,
    
    "display_context": {
        "<display_context_id>": {
            "model": "<model_location>",
            "texture": "<texture_location>",
            "animations": "<animations_location>",
            "display": "<display_location>"
        }
    }
}
```
> Где:
> - `name` - Имя модели (Существует по рофлу. Может что-то придумаю в будущем...).
> - `authors` - Список авторов модели (Существует по рофлу. Может что-то придумаю в будущем...).
> - `disable_ground_bobbing` - `true` для отключения покачивания предмета вверх-вниз.
> - `disable_ground_spinning` - `true` для отключения поворота предмета.
> - `display_context` - Контроллер профилей ресурсов.
>   - `display_context_id` - `ID` контекста отображения предмета:
>     - `none` - Контекст по умолчанию.
>     - `firstperson_lefthand` - От первого лица в левой руке.
>     - `firstperson_righthand` - От первого лица в правой руке.
>     - `thirdperson_firsthand` - От третьего лица в левой руке.
>     - `thirdperson_righthand` - От третьего лица в правой руке.
>     - `head` - На голове (В голове).
>     - `gui` - В меню (Контейнеры, Инвентари).
>     - `ground` - На земле (брошенный).
>     - `fixed` - В рамке.
>     - `on_shelf` - В полке.
>     - ---
>     - Описание профиля:
>       - Есть ресурсы лежат в том же месте, где и сам `properties.json` - то укажите в начале `./` и после имя файла ресурса. 
>       - `model` - Путь к файлу модели.
>       - `texture` - Путь к файлу текстуры для модели.
>       - `animations` - Путь к файлу с анимациями.
>       - `display` - Путь к файлу с отображениями.
>       - Не нужно для каждого контекста переписывать профиль. По умолчанию для каждого профиля, если не были указаны какие-то значения - эти значения берутся из контекста `none`.

</details>

</details>

---

<details>
<summary>Know problems / Известные проблемы</summary>

<details>
<summary>The animation stops playing for `item_display` after a pause. / У `item_display` после паузы перестаёт воспроизводится анимация</summary>

> Description / Описание:
> null

</details>

<details>
<summary>Animations stop playing after resources are reloaded. / После перезагрузки ресурсов анимации перестают воспроизводится</summary>

> Description / Описание:
> null

</details>

</details>

---

<details>
<summary>What's in the future / Что в будущем</summary>

[EN] In the future, I plan to make an advanced animation controller that would reproduce certain animations. For example, when you used an object, threw it away, held the cursor over the object, etc.

[RU] В будущем планирую сделать продвинутый контроллер анимации, который бы воспроизводил определённые анимации. К пример когда использовал предмет, выбросил, держишь курсор над предметом и т.д.

</details>