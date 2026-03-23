import os
import json

# Define constants
BASE_PATHS = [
    "../../common/src/main/resources/data/cobblemon/tags",
    "../../common/src/main/resources/data/minecraft/tags"
]
OUTPUT_PATHS = {
    'blocks': "../../docs/cobblemon-tags/BlocksTags.md",
    'entity_types': "../../docs/cobblemon-tags/EntityTypesTags.md",
    'items': "../../docs/cobblemon-tags/ItemTags.md",
    'biome': "../../docs/cobblemon-tags/BiomeTags.md",
}
DETAIL_TAGFILE_HEADERS = {
    'blocks': "# Block Tags\n\nThis file contains tags related to blocks in cobblemon.\n\n## Tags:\n",
    'entity_types': "# Entity Types Tags\n\nThis file contains tags related to different types of entities in cobblemon.\n\n## Tags:\n",
    'items': "# Item Tags\n\nThis file contains tags related to items in cobblemon.\n\n## Tags:\n",
    'biome': "# Biome Tags\n\nThis file contains tags related to different biomes in cobblemon, as well as some worldgen and special evolution tags.\n\n## Tags:\n",
}


def ensure_output_directory_exists():
    os.makedirs(os.path.dirname(next(iter(OUTPUT_PATHS.values()))), exist_ok=True)


def open_files_and_write_headers():
    output_files = {key: open(path, "w", encoding='utf-8') for key, path in OUTPUT_PATHS.items()}
    for key, file_handle in output_files.items():
        file_handle.write(DETAIL_TAGFILE_HEADERS[key])
    return output_files


def determine_category(root):
    if 'blocks' in root:
        return 'blocks'
    elif 'entity_types' in root:
        return 'entity_types'
    elif 'items' in root:
        return 'items'
    elif 'biome' in root:
        return 'biome'
    return None


def process_json_file(json_path, file_handle):
    with open(json_path, 'r', encoding='utf-8') as json_file:
        data = json.load(json_file)
        tags = [entry['id'] if isinstance(entry, dict) and 'id' in entry else entry for entry in data.get('values', [])]
        tag_directory = os.path.basename(os.path.dirname(json_path))
        json_file_base = os.path.basename(json_path)
        file_handle.write(f"\n<details>\n<summary><b>Tag:</b> #{tag_directory}:{json_file_base[:-5]}</summary>\n\n")
        for tag in tags:
            file_handle.write(f"- {tag}\n")
        file_handle.write("\n</details>\n")


def process_base_paths(output_files):
    for base_path in BASE_PATHS:
        for root, dirs, files in os.walk(base_path):
            category = determine_category(root)
            if category and category in output_files:
                for file_name in files:
                    if file_name.endswith('.json'):
                        process_json_file(os.path.join(root, file_name), output_files[category])


def close_files(output_files):
    for file_handle in output_files.values():
        file_handle.close()


def generate_detailed_tag_lists():
    ensure_output_directory_exists()
    output_files = open_files_and_write_headers()
    process_base_paths(output_files)
    close_files(output_files)

def main():
    generate_detailed_tag_lists()


if __name__ == "__main__":
    main()
