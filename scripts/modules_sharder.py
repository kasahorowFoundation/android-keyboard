#!/bin/python3

import os
import re
import sys
from typing import List


def _collect_gradle_modules(root_folder: str, projects_lst: str) -> List[str]:
    modules = list()
    with open(projects_lst, "r+") as settings_f:
        for line in settings_f.readlines():
            module_groups = re.findall(r"---\s+Project\s+\'([\w:_]+?)\'", line)
            modules.extend(module_groups)
    # Removing modules that do not have source files (thinking deployment, ime, pack, etc)
    def _has_src_file(module: str) -> bool:
        nonlocal root_folder
        src_folder = os.path.join(root_folder, module[1:].replace(":", "/"), "src")
        return os.path.isdir(src_folder)

    modules_with_src = filter(_has_src_file, modules)
    return list(modules_with_src)


if __name__ == "__main__":
    projects_lst = sys.argv[1]
    root_dir = sys.argv[2]
    sharding_type = sys.argv[3]
    tasks = sys.argv[4].split(",")
    output_file = sys.argv[5]
    modules_to_skip = list() if len(sys.argv) < 7 else sys.argv[6].split(",")

    print(f"modules from {root_dir} for sharding {sharding_type}:")
    all_modules = _collect_gradle_modules(root_dir, projects_lst)
    for module in all_modules:
        print(f" - {module}")
    for skip in modules_to_skip:
        if skip in all_modules:
            print(f" - remove {skip}")
            all_modules.remove(skip)
    
    extra_args = ""
    if sharding_type in ['addons_0', 'addons_1', 'addons_2']:
        packs = map(lambda apk: apk[:-4],
                    filter(lambda m: m.startswith(":addons:") and m.endswith(":apk") and not ":base:" in m, all_modules))
        shard_index = 0
        if sharding_type.endswith('_1'):
            shard_index = 1
        elif sharding_type.endswith('_2'):
            shard_index = 2
        modules_to_shard = list()
        # group 0 includes the base
        if shard_index == 0:
            modules_to_shard.extend([':addons:base', ':addons:base:apk'])

        for pack in packs:
            if shard_index % 3 == 0:
                modules_to_shard.extend([f"{pack}:pack", f"{pack}:apk"])
            shard_index += 1

    elif sharding_type == 'app':
        modules_to_shard = [":ime:app"]
        extra_args = "-PexcludeTestClasses=\"**/*AllSdkTest*\""
    elif sharding_type == 'app_all_sdks':
        modules_to_shard = [":ime:app"]
        extra_args = "--tests=\"*AllSdkTest*\""
    elif sharding_type == 'non_app':
        modules_to_shard = filter(lambda m: m != ":ime:app" and not m.startswith(":addons:"), all_modules)
    elif sharding_type in ['binaries_app']:
        if sharding_type == 'binaries_app':
            modules_to_shard = [":ime:app"]
        else:
            packs = filter(lambda m: m.startswith(":addons:") and m.endswith(":apk") and not ":base:" in m, all_modules)
            shard_index = 0
            if sharding_type.endswith('_1'):
                shard_index = 1
            elif sharding_type.endswith('_2'):
                shard_index = 2
            modules_to_shard = list()
            for pack in packs:
                if shard_index % 3 == 0:
                    modules_to_shard.append(pack)
                shard_index += 1
    else:
        raise Exception(f"Unkown sharding_type '{sharding_type}'")
    
    tasks_to_run = list()
    for module in modules_to_shard:
        tasks_to_run.extend([f"{module}:{task}" for task in tasks])

    gradle_cmd = ' '.join(tasks_to_run)
    print(f"gradle args: {gradle_cmd} {extra_args}")

    with open(output_file, "w+") as output_f:
        output_f.write(f"{gradle_cmd} {extra_args}")
