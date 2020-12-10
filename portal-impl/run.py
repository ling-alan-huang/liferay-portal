# Copyright (c) 2000-present Liferay, Inc. All rights reserved.
#
# This library is free software; you can redistribute it and/or modify it under
# the terms of the GNU Lesser General Public License as published by the Free
# Software Foundation; either version 2.1 of the License, or (at your option)
# any later version.
#
# This library is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
# FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
# details.

import glob
import datetime
import logging
import multiprocessing
import os
import sched
import sys
import time

from cts.app import sync


def main():

    if _has_flag('debug'):
        logging.basicConfig(
            format='%(name)-12s %(levelname)-8s %(message)s',
            level=logging.DEBUG)
    else:
        logging.basicConfig(
            format='%(name)-12s %(levelname)-8s %(message)s',
            level=logging.INFO)

    logging.info('Starting cts_sync scheduler')

    work_dir = sys.argv[len(sys.argv) - 1]

    if not os.path.exists(work_dir) or not os.path.isdir(work_dir):
        raise Exception('Sync dir not specified')

    scheduler = sched.scheduler(time.time, time.sleep)

    try:
        while True:
            stime = datetime.datetime.now()

            if _has_flag('debug'):
                stime = stime + datetime.timedelta(seconds=5)
            else:
                hour = 5
                minute = 0
                second = 0

                env_time = os.environ['CTS_SYNC_TIME'].split(':')

                if len(env_time) >= 2:
                    hour = int(env_time[0])
                    minute = int(env_time[1])

                if len(env_time) >= 3:
                    second = int(env_time[2])

                stime = stime.replace(hour=hour, minute=minute, second=second)
                stime = stime + datetime.timedelta(days=1)

            logging.info('Scheduling sync for %s' % stime.ctime())

            scheduler.enterabs(stime.timestamp(), 1, run_sync, (work_dir,))

            scheduler.run(blocking=True)

            if _has_flag('run-once'):
                logging.info(
                    'Sync scheduler was stopped because of the run-once '
                    'option; process was left running')

                while True:
                    time.sleep(1)
    except KeyboardInterrupt:
        sys.exit()


def run_sync(path):

    # Using os.walk or glob.glob has performance issues with our large
    # repositories. This method will only search 5 directories deep.

    config_paths = _config_locations(path)

    # Use multiprocessing. Do not use threading. Common implementation of
    # Python use a "global interpreter lock" effectively removing the benefits
    # of threading.

    with multiprocessing.Pool(processes=10) as pool:

        # Do not increase chunksize greater than 1. Execution time is not
        # normal and we not want long running process in the same chunk.

        pool.map(run_sync_worker, config_paths, 1)


def run_sync_worker(path):
    logging.info('Started syncronizing %s' % path)

    cwd = os.getcwd()

    try:
        os.chdir(path)

        sync.execute()
    except:
        logging.error('Failed to syncronize %s' % path)
        logging.error(sys.exc_info())
    finally:
        os.chdir(cwd)

    logging.info('Finished syncronizing %s' % path)


def _has_flag(name):
    if '--%s' % name in sys.argv:
        return True

    env_val = os.environ.get(name.upper().replace('-', '_'), '')

    if env_val.lower() in ['true', '1']:
        return True

    return False


def _config_locations(path, depth=0):
    if depth >= 5:
        return []

    configs = []

    for item in os.listdir(path):
        filepath = os.path.join(path, item)

        if os.path.isdir(filepath):
            configs.extend(
                _config_locations(filepath, depth + 1))
        elif item == 'cts_config.json':
            configs.append(path)

    return configs


if __name__ == "__main__":
    main()