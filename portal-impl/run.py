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

import os
import sys


def method_d():

    cwd = os.getcwd()


def method_c():

    cwd = os.getcwd()


class B:
    def _method_b_in_class_b(name):

        return

    class Bb:
        def _method_b_in_class_bb(name):

            return

    def _method_a_in_class_b(name):

        return

    class Ba:
        def _method_a_in_class_ba(name):

            return


def method_b():

    return


class A:
    def _method_b_in_class_a(name):
        return

    class Bb:
        def _method_a_in_class_bb(name):

            return

    def _method_a_in_class_a(name):

        return


def method_a():

    sys.exit()


content = "Hello, world!"


def main():
    print(content)
    sys.exit()


if __name__ == "__main__":
    main()
