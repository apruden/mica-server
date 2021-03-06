"""
Apply access on a study dataset.
"""

import sys
import mica.core
import mica.access

def add_arguments(parser):
    """
    Add command specific options
    """
    mica.access.add_permission_arguments(parser, True)
    parser.add_argument('id', help='Study dataset ID')

def do_command(args):
    """
    Execute access command
    """
    # Build and send requests
    try:
        mica.access.validate_args(args)

        request = mica.core.MicaClient.build(mica.core.MicaClient.LoginInfo.parse(args)).new_request()

        if args.verbose:
            request.verbose()

        # send request
        if args.delete:
            request.delete()
        else:
            request.put()

        try:
            response = request.resource(mica.access.do_ws(args, ['draft','study-dataset', args.id, 'accesses'])).send()
        except Exception, e:
            print Exception, e

        # format response
        if response.code != 204:
            print response.content

    except Exception, e:
        print e
        sys.exit(2)

    except pycurl.error, error:
        errno, errstr = error
        print >> sys.stderr, 'An error occurred: ', errstr
        sys.exit(2)
